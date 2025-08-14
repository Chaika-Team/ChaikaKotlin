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
import com.example.chaika.domain.usecases.FetchAndSaveProductsUseCase
import com.example.chaika.domain.usecases.GetAllProductsUseCase
import com.example.chaika.domain.usecases.GetCartItemsUseCase
import com.example.chaika.domain.usecases.GetPagedProductsUseCase
import com.example.chaika.ui.dto.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class TemplateViewModel @Inject constructor(
    private val getPagedTemplatesUseCase: GetPagedTemplatesUseCase,
    private val getTemplateDetailUseCase: GetTemplateDetailUseCase,
    private val applyTemplateUseCase: ApplyTemplateUseCase,
    private val getCartItemsUseCase: GetCartItemsUseCase,
    private val getAllProductsUseCase: GetAllProductsUseCase,
    private val addOpUseCase: AddOpUseCase,
    private val getPagedProductsUseCase: GetPagedProductsUseCase,
    private val fetchAndSaveProductsUseCase: FetchAndSaveProductsUseCase,
) : ViewModel() {
    val templatesPagingFlow = getPagedTemplatesUseCase().cachedIn(viewModelScope)

    private val _cartItems = MutableStateFlow<List<Product>>(emptyList())
    val cartItems: StateFlow<List<Product>> = _cartItems.asStateFlow()

    suspend fun getTemplateDetail(templateId: Int): TemplateDomain? {
        return try {
            Log.i("TemplateViewModel", "Trying to find template with id: $templateId")
            getTemplateDetailUseCase(templateId)
        } catch (e: Exception) {
            null
        }
    }
} 