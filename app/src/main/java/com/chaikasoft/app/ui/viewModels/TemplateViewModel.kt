package com.chaikasoft.app.ui.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.chaikasoft.app.domain.usecases.GetPagedTemplatesUseCase
import com.chaikasoft.app.domain.usecases.GetTemplateDetailUseCase
import com.chaikasoft.app.ui.state.TemplateDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TemplateViewModel @Inject constructor(
    getPagedTemplatesUseCase: GetPagedTemplatesUseCase,
    private val getTemplateDetailUseCase: GetTemplateDetailUseCase,
) : ViewModel() {
    val templatesPagingFlow = getPagedTemplatesUseCase().cachedIn(viewModelScope)

    private val _templateDetailState = MutableStateFlow<TemplateDetailUiState>(TemplateDetailUiState.Idle)
    val templateDetailState: StateFlow<TemplateDetailUiState> = _templateDetailState.asStateFlow()

    private var lastRequestedTemplateId: Int? = null
    private var detailLoadJob: Job? = null

    fun loadTemplateDetail(templateId: Int) {
        val currentState = _templateDetailState.value
        if (currentState is TemplateDetailUiState.Content && currentState.template.id == templateId) {
            return
        }

        lastRequestedTemplateId = templateId
        detailLoadJob?.cancel()
        detailLoadJob = viewModelScope.launch {
            _templateDetailState.value = TemplateDetailUiState.Loading
            try {
                Log.i("TemplateViewModel", "Trying to find template with id: $templateId")
                val template = getTemplateDetailUseCase(templateId)
                _templateDetailState.value = TemplateDetailUiState.Content(template)
            } catch (e: Exception) {
                Log.e("TemplateViewModel", "Failed to load template with id: $templateId", e)
                _templateDetailState.value = TemplateDetailUiState.Error(e)
            }
        }
    }

    fun retryLoadTemplateDetail() {
        lastRequestedTemplateId?.let(::loadTemplateDetail)
    }
}
