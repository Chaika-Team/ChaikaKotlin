package com.example.chaika.ui.viewModels

import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import androidx.annotation.Dimension
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.chaika.domain.models.CartItemDomain
import com.example.chaika.domain.models.ProductInfoDomain
import com.example.chaika.domain.usecases.AddItemToCartUseCase
import com.example.chaika.domain.usecases.FetchAndSaveProductsUseCase
import com.example.chaika.domain.usecases.GetCartItemsUseCase
import com.example.chaika.domain.usecases.GetPagedProductsUseCase
import com.example.chaika.domain.usecases.RemoveItemFromCartUseCase
import com.example.chaika.domain.usecases.UpdateItemQuantityInCartUseCase
import com.example.chaika.ui.dto.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val getPagedProductsUseCase: GetPagedProductsUseCase,
    private val fetchAndSaveProductsUseCase: FetchAndSaveProductsUseCase,
    private val addItemToCartUseCase: AddItemToCartUseCase,
    private val removeItemFromCartUseCase: RemoveItemFromCartUseCase,
    private val updateItemQuantityInCartUseCase: UpdateItemQuantityInCartUseCase,
    private val getCartItemsUseCase: GetCartItemsUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _productsState = mutableStateMapOf<Int, Product>()
    private val _uiState = MutableStateFlow<ScreenState>(ScreenState.Empty)
    private val _isLoading = MutableStateFlow(false)
    private val _cartItems = MutableStateFlow<List<Product>>(emptyList())
    private val _pagingDataFlow = MutableStateFlow<PagingData<Product>>(PagingData.empty())

    val uiState: StateFlow<ScreenState> = _uiState.asStateFlow()
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    val cartItems: StateFlow<List<Product>> = _cartItems.asStateFlow()
    val pagingDataFlow: StateFlow<PagingData<Product>> = _pagingDataFlow.asStateFlow()

    private var syncJob: Job? = null
    private var loadProductsJob: Job? = null

    init {
        viewModelScope.launch {
            if (_pagingDataFlow.value == PagingData.empty<Product>()) {
                loadInitialData()
            }
        }
        loadProducts()
        observeCartChanges()
        loadCartItems()
    }

    fun clearState() {
        _productsState.clear()
    }

    fun setProductList() {
        _uiState.update { ScreenState.ProductList }
    }

    fun setCart() {
        _uiState.update { ScreenState.Cart }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                fetchAndSaveProductsUseCase()
            } catch (e: Exception) {
                _uiState.update { ScreenState.Error }
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadProducts() {
        loadProductsJob?.cancel()
        loadProductsJob = viewModelScope.launch {
            getPagedProductsUseCase(
                pageSize = calculatePageSize(context = context)
            ).map { pagingData ->
                pagingData.map { domainProduct ->
                    _productsState.getOrPut(domainProduct.id) {
                        domainProduct.toUiModel()
                    }.copy(
                        isInCart = _cartItems.value.any { it.id == domainProduct.id },
                        quantity = _cartItems.value.find { it.id == domainProduct.id }?.quantity ?: 1
                    )
                }
            }
                .cachedIn(viewModelScope)
                .collect { pagingData ->
                    _pagingDataFlow.value = pagingData
                }
        }
    }

    private fun loadCartItems() {
        viewModelScope.launch {
            getCartItemsUseCase().collectLatest { cartItems ->
                _cartItems.value = cartItems.map { cartItem ->
                    Product(
                        id = cartItem.product.id,
                        name = cartItem.product.name,
                        description = cartItem.product.description,
                        image = cartItem.product.image,
                        price = cartItem.product.price,
                        isInCart = true,
                        quantity = cartItem.quantity
                    )
                }
                // Обновляем состояние продуктов при изменении корзины
                updateProductsState()
            }
        }
    }

    fun observeCartChanges() {
        syncJob?.cancel()
        syncJob = viewModelScope.launch {
            getCartItemsUseCase().collectLatest { _ ->
                updateProductsState()
                loadProducts()
            }
        }
    }

    private fun updateProductsState() {
        _cartItems.value.forEach { cartItem ->
            _productsState[cartItem.id]?.let { current ->
                _productsState[cartItem.id] = current.copy(
                    isInCart = true,
                    quantity = cartItem.quantity
                )
            }
        }

        // Обновляем продукты, которых нет в корзине
        _productsState.forEach { (id, product) ->
            if (!_cartItems.value.any { it.id == id }) {
                _productsState[id] = product.copy(
                    isInCart = false,
                    quantity = 1
                )
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
                addItemToCartUseCase(cartItem)
                // Изменения подхватятся через observeCartChanges
            }
        }
    }

    fun removeFromCart(productId: Int) {
        viewModelScope.launch {
            removeItemFromCartUseCase(productId)
            // Изменения подхватятся через observeCartChanges
        }
    }

    fun updateCartQuantity(productId: Int, change: Int) {
        viewModelScope.launch {
            _cartItems.value.find { it.id == productId }?.let { current ->
                val newQuantity = current.quantity + change
                if (newQuantity > 0) {
                    updateItemQuantityInCartUseCase(
                        itemId = productId,
                        newQuantity = newQuantity,
                        availableQuantity = 50
                    )
                    Log.d("ProductViewModel", "Updated quantity of $productId to $newQuantity")
                    // Изменения подхватятся через observeCartChanges
                } else {
                    removeFromCart(productId)
                    Log.d("ProductViewModel", "Removed $productId from cart")
                }
            } ?: Log.d("ProductViewModel", "Product with ID $productId not found in _cartItems")
        }
    }

    fun updateQuantity(productId: Int, change: Int) {
        viewModelScope.launch {
            _productsState[productId]?.let { current ->
                val newQuantity = current.quantity + change
                if (newQuantity > 0) {
                    updateItemQuantityInCartUseCase(
                        itemId = productId,
                        newQuantity = newQuantity,
                        availableQuantity = 50
                    )
                    // Изменения подхватятся через observeCartChanges
                } else {
                    removeFromCart(productId)
                }
            } ?: Log.d("ProductViewModel", "Product with ID $productId not found in _productsState")
        }
    }

    fun calculatePageSize(
        context: Context,
        @Dimension(unit = Dimension.DP) itemHeightDp: Int = 210
    ): Int {
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        val screenHeightPx = displayMetrics.heightPixels
        val itemHeightPx = (itemHeightDp * displayMetrics.density).toInt()
        val visibleItemsCount = (screenHeightPx / itemHeightPx) * 2
        return visibleItemsCount * 3
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

    sealed class ScreenState {
        object Empty : ScreenState()
        object ProductList : ScreenState()
        object Cart : ScreenState()
        object Error : ScreenState()
    }
}