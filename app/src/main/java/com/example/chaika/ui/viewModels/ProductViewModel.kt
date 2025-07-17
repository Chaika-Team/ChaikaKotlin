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
import com.example.chaika.domain.models.ConductorDomain
import com.example.chaika.domain.models.ProductInfoDomain
import com.example.chaika.domain.usecases.AddItemToCartUseCase
import com.example.chaika.domain.usecases.AddOpUseCase
import com.example.chaika.domain.usecases.FetchAndSaveProductsUseCase
import com.example.chaika.domain.usecases.GetAllConductorsUseCase
import com.example.chaika.domain.usecases.GetCartItemsUseCase
import com.example.chaika.domain.usecases.GetPackageItemUseCase
import com.example.chaika.domain.usecases.GetPagedProductsUseCase
import com.example.chaika.domain.usecases.RemoveItemFromCartUseCase
import com.example.chaika.domain.usecases.SoldCardOpUseCase
import com.example.chaika.domain.usecases.SoldCashOpUseCase
import com.example.chaika.domain.usecases.UpdateQuantityUnlimitedUseCase
import com.example.chaika.domain.usecases.UpdateQuantityWithLimitUseCase
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
    private val updateQuantityUnlimitedUseCase: UpdateQuantityUnlimitedUseCase,
    private val updateQuantityWithLimitUseCase: UpdateQuantityWithLimitUseCase,
    private val getCartItemsUseCase: GetCartItemsUseCase,
    private val getPackageItemsUseCase: GetPackageItemUseCase,
    private val soldCashOpUseCase: SoldCashOpUseCase,
    private val soldCardOpUseCase: SoldCardOpUseCase,
    private val getAllConductorsUseCase: GetAllConductorsUseCase,
    private val addOpUseCase: AddOpUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _productsCartState = mutableStateMapOf<Int, Product>()
    private val _uiState = MutableStateFlow<ScreenState>(ScreenState.Empty)
    private val _isLoading = MutableStateFlow(false)
    private val _cartItems = MutableStateFlow<List<Product>>(emptyList())
    private val _pagingDataFlow = MutableStateFlow<PagingData<Product>>(PagingData.empty())
    private val _conductors = MutableStateFlow<List<ConductorDomain>>(emptyList())
    val conductors: StateFlow<List<ConductorDomain>> = _conductors.asStateFlow()
    private val _packageItems = MutableStateFlow<List<Product>>(emptyList())
    val packageItems: StateFlow<List<Product>> = _packageItems.asStateFlow()

    val uiState: StateFlow<ScreenState> = _uiState.asStateFlow()
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    val cartItems: StateFlow<List<Product>> = _cartItems.asStateFlow()
    val pagingDataFlow: StateFlow<PagingData<Product>> = _pagingDataFlow.asStateFlow()

    private var syncJob: Job? = null
    private var loadProductsJob: Job? = null

    init {
        loadInitialData()
        loadProducts()
        observeCartChanges()
        loadCartItems()
        loadPackageItems()
        loadConductors()
    }

    fun setCart() {
        _uiState.update { ScreenState.Cart }
    }

    private fun loadInitialData() {
        Log.d("ProductViewModel", "loadInitialData called")
        viewModelScope.launch {
            _isLoading.value = true
            try {
                fetchAndSaveProductsUseCase()
            } catch (e: Exception) {
                Log.e("ProductViewModel", "loadInitialData: error:  [${e.message}]", e)
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
                    _productsCartState.getOrPut(domainProduct.id) {
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

    private fun loadPackageItems() {
        viewModelScope.launch {
            try {
                getPackageItemsUseCase().collectLatest { packageItemDomains ->
                    val validPackageItems = packageItemDomains.filter { packageItemDomain ->
                        packageItemDomain.currentQuantity > 0
                    }
                    
                    _packageItems.value = validPackageItems.map { packageItemDomain ->
                        val product = packageItemDomain.productInfoDomain
                        val cartItem = _cartItems.value.find { it.id == product.id }
                        Product(
                            id = product.id,
                            name = product.name,
                            description = product.description,
                            image = product.image,
                            price = product.price,
                            isInPackage = true,
                            isInCart = cartItem != null,
                            quantity = cartItem?.quantity ?: 1,
                            packageQuantity = packageItemDomain.currentQuantity
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Error loading package items:  [${e.message}]", e)
                _uiState.update { ScreenState.Error }
            }
        }
    }

    private fun loadConductors() {
        viewModelScope.launch {
            try {
                getAllConductorsUseCase().collectLatest { _conductors.value = it }
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Error loading conductors:  [ [${e.message}]", e)
                _uiState.update { ScreenState.Error }
            }
        }
    }

    fun observeCartChanges() {
        syncJob?.cancel()
        syncJob = viewModelScope.launch {
            getCartItemsUseCase().collectLatest { _ ->
                updateProductsState()
                loadProducts()
                loadPackageItems()
            }
        }
    }

    private fun updateProductsState() {
        _cartItems.value.forEach { cartItem ->
            _productsCartState[cartItem.id]?.let { current ->
                _productsCartState[cartItem.id] = current.copy(
                    isInCart = true,
                    quantity = cartItem.quantity
                )
            }
        }

        _productsCartState.forEach { (id, product) ->
            if (!_cartItems.value.any { it.id == id }) {
                _productsCartState[id] = product.copy(
                    isInCart = false,
                    quantity = 1
                )
            }
        }
    }

    fun addToCart(product: Product) {
        viewModelScope.launch {
            val cartItem = CartItemDomain(
                product = product.toDomain(),
                quantity = 1
            )
            addItemToCartUseCase(cartItem)
        }
    }

    fun removeFromCart(productId: Int) {
        viewModelScope.launch {
            removeItemFromCartUseCase(productId)
        }
    }

    fun changeCartQuantity(productId: Int, change: Int) {
        viewModelScope.launch {
            val cartItem = _cartItems.value.find { it.id == productId }
            val newQuantity = (cartItem?.quantity ?: 0) + change

            if (newQuantity <= 0) {
                removeFromCart(productId)
                Log.d("ProductViewModel", "Removed $productId from cart")
                return@launch
            }

            val isPackage = _packageItems.value.any { it.id == productId }
            val updated = if (isPackage) {
                updateQuantityWithLimitUseCase(productId, newQuantity)
            } else {
                updateQuantityUnlimitedUseCase(productId, newQuantity)
            }

            if (updated) {
                Log.d("ProductViewModel", "Updated quantity of $productId to $newQuantity in cart")
            } else {
                Log.d("ProductViewModel", "Failed to update quantity for $productId")
            }
        }
    }

    fun addToPackage(conductorId: Int) {
        viewModelScope.launch {
            try {
                addOpUseCase(conductorId)
                Log.d("ProductViewModel", "Successfully added to package for conductor $conductorId")
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Error adding to package for conductor $conductorId", e)
                _uiState.update { ScreenState.Error }
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

    fun payByCash(conductorId: Int) {
        viewModelScope.launch {
            if (_cartItems.value.isEmpty()) {
                Log.w("ProductViewModel", "Попытка оплаты наличными при пустой корзине")
                return@launch
            }
            try {
                soldCashOpUseCase.invoke(conductorId)
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Ошибка при оплате наличными: ", e)
            }
        }
    }

    fun payByCard(conductorId: Int) {
        viewModelScope.launch {
            if (_cartItems.value.isEmpty()) {
                Log.w("ProductViewModel", "Попытка оплаты картой при пустой корзине")
                return@launch
            }
            try {
                soldCardOpUseCase(conductorId)
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Ошибка при оплате картой: ", e)
            }
        }
    }

    sealed class ScreenState {
        object Empty : ScreenState()
        object ProductList : ScreenState()
        object Package: ScreenState()
        object Cart : ScreenState()
        object Error : ScreenState()
    }
}