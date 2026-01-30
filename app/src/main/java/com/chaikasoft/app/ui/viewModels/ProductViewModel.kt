package com.chaikasoft.app.ui.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.chaikasoft.app.domain.models.CartItemDomain
import com.chaikasoft.app.domain.models.ProductInfoDomain
import com.chaikasoft.app.domain.usecases.FetchAndSaveProductsUseCase
import com.chaikasoft.app.domain.usecases.GetPagedProductsUseCase
import com.chaikasoft.app.ui.dto.Product
import com.chaikasoft.app.ui.mappers.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val getPagedProductsUseCase: GetPagedProductsUseCase,
    private val fetchAndSaveProductsUseCase: FetchAndSaveProductsUseCase,
) : ViewModel() {

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _syncError = MutableSharedFlow<String>()
    val syncError: Flow<String> = _syncError

    private val cartItemsFlow = MutableStateFlow<List<CartItemDomain>>(emptyList())

    private var isInitialized = false
    private var syncJob: Job? = null

    /**
     * Основной Flow с продуктами
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val productsFlow: Flow<PagingData<Product>> =
        _searchQuery
            .debounceExceptFirst(300)
            .distinctUntilChanged()
            .flatMapLatest { query: String ->
                getPagedProductsUseCase(query)
            }
            .map { pagingData: PagingData<ProductInfoDomain> ->
                pagingData.map { product: ProductInfoDomain ->
                    product.toUiModel()
                }
            }
            .cachedIn(viewModelScope)


    fun onSearchChange(query: String) {
        _searchQuery.value = query
    }

    /**
     * Инициализация данных - вызывается только один раз
     */
    fun attachCart(cartItems: StateFlow<List<CartItemDomain>>) {
        if (isInitialized) return
        isInitialized = true

        viewModelScope.launch {
            cartItems.collect {
                cartItemsFlow.value = it
            }
        }

        syncProductsInBackground()
    }

    /**
     * Debounce, который НЕ задерживает первое значение.
     * Использует channelFlow для правильного управления состоянием.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun <T> Flow<T>.debounceExceptFirst(timeoutMillis: Long): Flow<T> = channelFlow {
        var isFirst = true
        var latestValue: T? = null
        var hasValue = false
        var delayJob: Job? = null

        collectLatest { value ->
            if (isFirst) {
                isFirst = false
                send(value)
            } else {
                delayJob?.cancel()
                latestValue = value
                hasValue = true
                delayJob = launch {
                    delay(timeoutMillis)
                    if (hasValue) {
                        send(latestValue as T)
                        hasValue = false
                    }
                }
            }
        }
    }.buffer(Channel.RENDEZVOUS)

    private fun mapWithCart(
        pagingData: PagingData<ProductInfoDomain>,
        cartItems: List<CartItemDomain>
    ): PagingData<Product> {
        val cartItemsMap = cartItems
            .filter { it.quantity >= 1 }
            .associateBy { it.product.id }

        return pagingData.map { product ->
            cartItemsMap[product.id]?.toUiModel()
                ?: product.toUiModel()
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
                _syncError.emit("Ошибка синхронизации: ${e.localizedMessage}")
            } finally {
                _isSyncing.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        syncJob?.cancel()
    }
}