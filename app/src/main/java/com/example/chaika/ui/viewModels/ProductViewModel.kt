package com.example.chaika.ui.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.insertSeparators
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

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var loadProductsJob: Job? = null

    fun loadInitialData(cartItems: StateFlow<List<CartItemDomain>>) {
        loadProductsJob?.cancel()
        _isLoading.value = true
        viewModelScope.launch {
            try {
                fetchProducts().also {
                    loadProducts(cartItems)
                }
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Error in loadInitialData: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun fetchProducts(): Boolean {
        return try {
            Log.d("ProductViewModel", "Fetching products started")
            fetchAndSaveProductsUseCase()
            Log.d("ProductViewModel", "Fetching products completed")
            true
        } catch (e: Exception) {
            Log.e("ProductViewModel", "Error in fetchProducts: ${e.message}", e)
            false
        }
    }

    fun loadProducts(cartItems: StateFlow<List<CartItemDomain>>) {
        loadProductsJob?.cancel()
        loadProductsJob = viewModelScope.launch {
            getPagedProductsUseCase()
                .cachedIn(viewModelScope)
                .combine(cartItems) { pagingData, cartItemsList ->
                    pagingData.map { productDomain ->
                        cartItemsList.find { it.product.id == productDomain.id }
                            ?.takeIf { it.quantity >= 1 }
                            ?.toUiModel()
                            ?: productDomain.toUiModel()
                    }
                }
                .collect { pagingData ->
                    _productsFlow.value = pagingData
                }
        }
    }

    fun clearProductState() {
        _productsFlow.value = PagingData.empty()
        _isLoading.value = false
        loadProductsJob?.cancel()
        loadProductsJob = null
    }
}