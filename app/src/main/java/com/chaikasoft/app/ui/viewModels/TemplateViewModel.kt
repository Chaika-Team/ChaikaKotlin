package com.chaikasoft.app.ui.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.chaikasoft.app.domain.usecases.GetPagedTemplatesUseCase
import com.chaikasoft.app.domain.usecases.GetTemplateDetailUseCase
import com.chaikasoft.app.domain.models.TemplateDomain
import com.chaikasoft.app.ui.dto.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class TemplateViewModel @Inject constructor(
    private val getPagedTemplatesUseCase: GetPagedTemplatesUseCase,
    private val getTemplateDetailUseCase: GetTemplateDetailUseCase,
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