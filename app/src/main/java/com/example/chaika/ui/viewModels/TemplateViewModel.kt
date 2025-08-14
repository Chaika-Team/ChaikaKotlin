package com.example.chaika.ui.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.chaika.domain.usecases.GetPagedTemplatesUseCase
import com.example.chaika.domain.usecases.GetTemplateDetailUseCase
import com.example.chaika.domain.models.TemplateDomain
import com.example.chaika.domain.usecases.AddOpUseCase
import com.example.chaika.domain.usecases.ApplyTemplateUseCase
import com.example.chaika.domain.usecases.GetAllProductsUseCase
import com.example.chaika.domain.usecases.GetCartItemsUseCase
import com.example.chaika.domain.usecases.GetPagedProductsUseCase
import com.example.chaika.ui.dto.Product
import com.example.chaika.ui.viewModels.ProductViewModel.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TemplateViewModel @Inject constructor(
    private val getPagedTemplatesUseCase: GetPagedTemplatesUseCase,
    private val getTemplateDetailUseCase: GetTemplateDetailUseCase,
    private val applyTemplateUseCase: ApplyTemplateUseCase,
    private val getCartItemsUseCase: GetCartItemsUseCase,
    private val getAllProductsUseCase: GetAllProductsUseCase,
    private val addOpUseCase: AddOpUseCase
) : ViewModel() {
    val templatesPagingFlow = getPagedTemplatesUseCase().cachedIn(viewModelScope)

    private val _cartItems = MutableStateFlow<List<Product>>(emptyList())
    val cartItems: StateFlow<List<Product>> = _cartItems.asStateFlow()

    init {
        getAllProductsUseCase()
    }

    suspend fun getTemplateDetail(templateId: Int): TemplateDomain? {
        return try {
            Log.i("TemplateViewModel", "Trying to find template with id: $templateId")
            getTemplateDetailUseCase(templateId)
        } catch (e: Exception) {
            null
        }
    }

    fun applyTemplate(template: TemplateDomain) {
        viewModelScope.launch {
//            applyTemplateUseCase(template)
            loadCartItems()
        }
    }

    fun addToPackage(conductorId: Int) {
        viewModelScope.launch {
            try {
                addOpUseCase(conductorId)
                Log.d("ProductViewModel", "Successfully added to package for conductor $conductorId")
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Error adding to package for conductor $conductorId", e)
            }
        }
    }

    private suspend fun loadCartItems() {
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
//                updateProductsState()
        }
    }
} 