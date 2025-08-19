package com.example.chaika.ui.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.chaika.domain.models.CartItemDomain
import com.example.chaika.domain.usecases.FetchAndSaveProductsUseCase
import com.example.chaika.domain.usecases.GetPagedProductsUseCase
import com.example.chaika.ui.dto.Product
import com.example.chaika.ui.mappers.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val getPagedProductsUseCase: GetPagedProductsUseCase,
    private val fetchAndSaveProductsUseCase: FetchAndSaveProductsUseCase,
) : ViewModel() {

    private val _productsFlow = MutableStateFlow<PagingData<Product>>(PagingData.empty())
    val productsFlow: StateFlow<PagingData<Product>> = _productsFlow.asStateFlow()

    // Индикатор только для фоновой синхронизации, не для самого списка
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private var syncJob: Job? = null

    private val pagedDomainFlow = getPagedProductsUseCase()
        .cachedIn(viewModelScope)

    fun loadInitialData(cartItems: StateFlow<List<CartItemDomain>>) {
        // Запускаем один поток для пагинации (не пересоздаем каждый раз)
        startProductsFlow(cartItems)

        // Параллельно запускаем фоновую синхронизацию
        syncProductsInBackground()
    }

    private fun startProductsFlow(cartItems: StateFlow<List<CartItemDomain>>) {
        viewModelScope.launch {
            pagedDomainFlow
                .combine(cartItems) { pagingData, cartItemsList ->
                    val cartItemsMap = cartItemsList
                        .filter { it.quantity >= 1 }
                        .associateBy { it.product.id }

                    pagingData.map { productDomain ->
                        cartItemsMap[productDomain.id]?.toUiModel()
                            ?: productDomain.toUiModel()
                    }
                }
                .catch { exception ->
                    Log.e("ProductViewModel", "Error in products flow: ${exception.message}", exception)
                    // Можно эмитить пустые данные или сохранить последнее состояние
                    emit(PagingData.empty())
                }
                .collect { pagingData ->
                    _productsFlow.value = pagingData
                }
        }
    }

    private fun syncProductsInBackground() {
        syncJob?.cancel()
        syncJob = viewModelScope.launch {
            _isSyncing.value = true
            try {
                Log.d("ProductViewModel", "Background sync started")
                fetchAndSaveProductsUseCase()
                Log.d("ProductViewModel", "Background sync completed")
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Error in background sync: ${e.message}", e)
            } finally {
                _isSyncing.value = false
            }
        }
    }

    // Метод для принудительного обновления
    fun refreshProducts() {
        syncProductsInBackground()
    }

    fun clearProductState() {
        _productsFlow.value = PagingData.empty()
        _isSyncing.value = false
        syncJob?.cancel()
        syncJob = null
    }
}