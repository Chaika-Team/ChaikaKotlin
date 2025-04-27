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
import kotlinx.coroutines.flow.first
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

    private val _uiState = MutableStateFlow(ProductScreenUiState())
    val uiState: StateFlow<ProductScreenUiState> = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _pagingDataFlow = MutableStateFlow<PagingData<Product>>(PagingData.empty())
    val pagingDataFlow: StateFlow<PagingData<Product>> = _pagingDataFlow.asStateFlow()

    private var _syncJob: Job? = null

    private var loadProductsJob: Job? = null


    init {
        loadInitialData()
        loadProducts()
        syncWithCartOnChange()
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
        loadProductsJob?.cancel()
        loadProductsJob = viewModelScope.launch {
            getPagedProductsUseCase(
                pageSize = calculatePageSize(context = context)
            ).map { pagingData ->
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
        Log.d("ProductViewModel", "${_pagingDataFlow.value}")

    }

    fun syncWithCartOnChange() {
        if (_syncJob?.isActive == true) return
        _syncJob = viewModelScope.launch {
            val cartItems = getCartItemsUseCase().first()
            for (item in cartItems) {
                _productsState[item.product.id] = Product(
                    id = item.product.id,
                    name = item.product.name,
                    description = item.product.description,
                    image = item.product.image,
                    price = item.product.price,
                    isInCart = item.quantity > 0,
                    quantity = item.quantity
                )
            }
            loadProducts()
        }
    }

    fun syncWithCartOnRemove() {
        if (_syncJob?.isActive == true) return
        _syncJob = viewModelScope.launch {
            val cartItems = getCartItemsUseCase().first()
            val updatedProducts = _productsState.mapValues { (id, product) ->
                val isInCart = cartItems.any { it.product.id == id }
                product.copy(isInCart = isInCart)
            }.toMutableMap()

            _productsState.putAll(updatedProducts)

             loadProducts()
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
                    syncWithCartOnChange()
                }
            }
        }
    }

    fun removeFromCart(productId: Int) {
        viewModelScope.launch {
            _productsState[productId]?.let { product ->
                val success = removeItemFromCartUseCase(itemId = productId)

                if (success) {
                    syncWithCartOnRemove()
                }
            }
        }
    }

    fun updateQuantity(productId: Int, change: Int) {
        viewModelScope.launch {
            _productsState[productId]?.let { current ->
                val newQuantity = (current.quantity + change)

                if (newQuantity > 0) {
                     val success = updateItemQuantityInCartUseCase(
                        itemId = productId,
                        newQuantity = newQuantity,
                        availableQuantity = 50
                    )

                    if (success) {
                        loadProducts()
                        syncWithCartOnChange()
                    }
                } else {
                    removeFromCart(productId)
                }
            }
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
}

data class ProductScreenUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)