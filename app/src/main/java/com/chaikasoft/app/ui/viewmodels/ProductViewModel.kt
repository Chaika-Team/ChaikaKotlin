package com.chaikasoft.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.chaikasoft.app.domain.models.CartItemDomain
import com.chaikasoft.app.domain.models.ProductInfoDomain
import com.chaikasoft.app.domain.usecases.GetPagedProductsUseCase
import com.chaikasoft.app.ui.dto.Product
import com.chaikasoft.app.ui.mappers.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val getPagedProductsUseCase: GetPagedProductsUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val cartItemsFlow = MutableStateFlow<List<CartItemDomain>>(emptyList())

    private var isInitialized = false

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
    }

    /**
     * Debounce, который НЕ задерживает первое значение.
     * Использует channelFlow для правильного управления состоянием.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun <T> Flow<T>.debounceExceptFirst(timeoutMillis: Long): Flow<T> = channelFlow {
        var isFirst = true

        collectLatest { value ->
            if (isFirst) {
                isFirst = false
                send(value)
            } else {
                delay(timeoutMillis)
                send(value)
            }
        }
    }
}
