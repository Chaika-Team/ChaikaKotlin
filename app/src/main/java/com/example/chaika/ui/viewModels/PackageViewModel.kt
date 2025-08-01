package com.example.chaika.ui.viewModels

import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import androidx.annotation.Dimension
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
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PackageViewModel @Inject constructor(
    private val getPagedProductsUseCase: GetPagedProductsUseCase,
    private val fetchAndSaveProductsUseCase: FetchAndSaveProductsUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _productsFlow = MutableStateFlow<PagingData<Product>>(PagingData.empty())
    val productsFlow: StateFlow<PagingData<Product>> = _productsFlow.asStateFlow()

    private var loadProductsJob: Job? = null

    fun loadInitialData() {
        Log.d("ProductViewModel", "loadInitialData called")
        viewModelScope.launch {
            try {
                fetchAndSaveProductsUseCase()
            } catch (e: Exception) {
                Log.e("ProductViewModel", "loadInitialData: error:  [${e.message}]", e)
            }
        }
    }

    fun loadProducts(cartItems: StateFlow<List<CartItemDomain>>) {
        loadProductsJob?.cancel()
        loadProductsJob = viewModelScope.launch {
            getPagedProductsUseCase()
                .cachedIn(viewModelScope)
                .combine(cartItems) { pagingData, cartItemsList ->
                    pagingData.map { productDomain ->
                        val cartItem = cartItemsList.find { it.product.id == productDomain.id }
                        if (cartItem != null) {
                            // Если товар есть в корзине, проверяем количество
                            if (cartItem.quantity >= 1) {
                                cartItem.toUiModel()
                            } else {
                                // Если количество меньше 1, считаем что товара нет в корзине
                                productDomain.toUiModel().copy(isInCart = false)
                            }
                        } else {
                            productDomain.toUiModel()
                        }
                    }
                }
                .collect { pagingData ->
                    _productsFlow.value = pagingData
                }
        }
    }

    fun clearProductState() {
        _productsFlow.update { PagingData.empty() }
        loadProductsJob?.cancel()
        loadProductsJob = null
    }

    private fun calculatePageSize(
        context: Context,
        @Dimension(unit = Dimension.DP) itemHeightDp: Int = 210
    ): Int {
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        val screenHeightPx = displayMetrics.heightPixels
        val itemHeightPx = (itemHeightDp * displayMetrics.density).toInt()
        val visibleItemsCount = (screenHeightPx / itemHeightPx) * 2
        return visibleItemsCount * 3
    }
}