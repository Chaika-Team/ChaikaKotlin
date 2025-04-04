package com.example.chaika.ui.viewModels


import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.chaika.data.room.repo.RoomProductInfoRepositoryInterface
import com.example.chaika.domain.models.CartItemDomain
import com.example.chaika.domain.models.ProductInfoDomain
import com.example.chaika.domain.usecases.AddItemToCartUseCase
import com.example.chaika.domain.usecases.FetchAndSaveProductsUseCase
import com.example.chaika.domain.usecases.GetCartItemsUseCase
import com.example.chaika.domain.usecases.GetPagedProductsUseCase
import com.example.chaika.domain.usecases.UpdateItemQuantityInCartUseCase
import com.example.chaika.ui.dto.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val getPagedProductsUseCase: GetPagedProductsUseCase,
    private val fetchAndSaveProductsUseCase: FetchAndSaveProductsUseCase,
    private val addItemToCartUseCase: AddItemToCartUseCase,
    private val updateItemQuantityInCartUseCase: UpdateItemQuantityInCartUseCase,
    private val getCartItemsUseCase: GetCartItemsUseCase
) : ViewModel() {

    private val _productsState = mutableStateMapOf<Int, Product>()
    val productsState: SnapshotStateMap<Int, Product> = _productsState

    private val _updateTrigger = MutableStateFlow(0)
    val updateTrigger: StateFlow<Int> = _updateTrigger.asStateFlow()

    private val _uiState = MutableStateFlow(ProductScreenUiState())
    val uiState: StateFlow<ProductScreenUiState> = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _pagingDataFlow = MutableStateFlow<PagingData<Product>>(PagingData.empty())
    val pagingDataFlow: StateFlow<PagingData<Product>> = _pagingDataFlow.asStateFlow()

    private var _syncJob: Job? = null

//    val pagedProducts: Flow<PagingData<Product>> = getPagedProductsUseCase()
//        .combine(updateTrigger) { pagingData, _ ->
//            pagingData.map { domainProduct ->
//                _productsState.getOrPut(domainProduct.id) {
//                    domainProduct.toUiModel()
//                }
//            }
//        }
//        .cachedIn(viewModelScope)

    init {
        loadInitialData()
        loadProducts()
        syncWithCart()
    }

    fun clearState() {
        _productsState.clear()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                fetchAndSaveProductsUseCase()
                _uiState.update { it.copy(error = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            getPagedProductsUseCase()
                .map { pagingData ->
                    pagingData.map { domainProduct ->
                        _productsState.getOrPut(domainProduct.id) {
                            domainProduct.toUiModel()
                        }
                    }
                }
                .cachedIn(viewModelScope)
                .collect { pagingData ->
                    _pagingDataFlow.value = pagingData
                }
        }
    }

    fun syncWithCart() {
        if (_syncJob?.isActive == true) return
        _syncJob = viewModelScope.launch {
            viewModelScope.launch {
                val cartItems = getCartItemsUseCase().first()
                _productsState.forEach { (id, product) ->
                    val cartItem = cartItems.find { it.product.id == id }
                    _productsState[id] = product.copy(
                        isInCart = cartItem != null,
                        quantity = cartItem?.quantity ?: 1
                    )
                }
                loadProducts()
            }
        }
    }

    fun addToCart(productId: Int) {
        viewModelScope.launch {
            _productsState[productId]?.let { product ->
                val cartItem = CartItemDomain(
                    product = product.toDomain(),
                    quantity = product.quantity
                )

                val success = addItemToCartUseCase(cartItem)

                if (success) {
                    syncWithCart() // Синхронизируем после изменения
                }
            }
        }
    }

    fun updateQuantity(productId: Int, change: Int) {
        viewModelScope.launch {
            _productsState[productId]?.let { current ->
                val newQuantity = (current.quantity + change).coerceAtLeast(1)

                val success = updateItemQuantityInCartUseCase(
                    itemId = productId,
                    newQuantity = newQuantity,
                    availableQuantity = Int.MAX_VALUE
                )

                if (success) {
                    syncWithCart() // Синхронизируем после изменения
                }
            }
        }
    }

    private fun ProductInfoDomain.toUiModel(): Product {
        return Product(
            id = this.id,
            name = this.name,
            description = this.description,
            image = this.image,
            price = this.price,
            isInCart = false,
            quantity = 1
        )
    }


}

data class ProductScreenUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)