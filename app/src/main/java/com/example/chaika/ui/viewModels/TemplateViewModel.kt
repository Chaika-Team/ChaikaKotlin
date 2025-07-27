package com.example.chaika.ui.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.chaika.domain.usecases.GetPagedTemplatesUseCase
import com.example.chaika.domain.usecases.GetTemplateDetailUseCase
import com.example.chaika.domain.models.TemplateDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TemplateViewModel @Inject constructor(
    private val getPagedTemplatesUseCase: GetPagedTemplatesUseCase,
    private val getTemplateDetailUseCase: GetTemplateDetailUseCase
) : ViewModel() {
    val templatesPagingFlow = getPagedTemplatesUseCase().cachedIn(viewModelScope)

    suspend fun getTemplateDetail(templateId: Int): TemplateDomain? {
        return try {
            Log.i("TemplateViewModel", "Trying to find template with id: $templateId")
            getTemplateDetailUseCase(templateId)
        } catch (e: Exception) {
            null
        }
    }
} 