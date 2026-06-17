package com.chaikasoft.app.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.chaikasoft.app.domain.common.AppError
import com.chaikasoft.app.domain.common.RemoteResult
import com.chaikasoft.app.domain.models.ResolvedTemplateDetailDomain
import com.chaikasoft.app.domain.usecases.GetPagedTemplatesUseCase
import com.chaikasoft.app.domain.usecases.GetResolvedTemplateDetailUseCase
import com.chaikasoft.app.ui.mappers.AppErrorUiMapper
import com.chaikasoft.app.ui.state.TemplateDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class TemplateViewModel @Inject constructor(
    getPagedTemplatesUseCase: GetPagedTemplatesUseCase,
    private val getResolvedTemplateDetailUseCase: GetResolvedTemplateDetailUseCase
) : ViewModel() {
    val templatesPagingFlow = getPagedTemplatesUseCase().cachedIn(viewModelScope)

    private val _templateDetailState =
        MutableStateFlow<TemplateDetailUiState>(TemplateDetailUiState.Idle)
    val templateDetailState: StateFlow<TemplateDetailUiState> = _templateDetailState.asStateFlow()

    private var lastRequestedTemplateId: Int? = null
    private var detailLoadJob: Job? = null

    fun loadTemplateDetail(templateId: Int) {
        val currentState = _templateDetailState.value
        if (currentState is TemplateDetailUiState.Content &&
            currentState.detail.template.id == templateId
        ) {
            return
        }

        lastRequestedTemplateId = templateId
        detailLoadJob?.cancel()
        detailLoadJob = viewModelScope.launch {
            _templateDetailState.value = TemplateDetailUiState.Loading
            Log.i("TemplateViewModel", "Trying to find template with id: $templateId")
            runCatching {
                getResolvedTemplateDetailUseCase(templateId)
            }.fold(
                onSuccess = { result -> result.applyToState() },
                onFailure = { error -> _templateDetailState.value = error.toUiState(templateId) }
            )
        }
    }

    fun retryLoadTemplateDetail() {
        lastRequestedTemplateId?.let(::loadTemplateDetail)
    }

    private fun AppError.toUiState(): TemplateDetailUiState.Error {
        val ui = AppErrorUiMapper.map(this)
        return TemplateDetailUiState.Error(ui.messageRes, ui.retryable)
    }

    private fun RemoteResult<ResolvedTemplateDetailDomain>.applyToState() {
        _templateDetailState.value = when (this) {
            is RemoteResult.Success -> TemplateDetailUiState.Content(data)
            is RemoteResult.Failure -> error.toUiState()
        }
    }

    private fun Throwable.toUiState(templateId: Int): TemplateDetailUiState.Error {
        if (this is CancellationException) throw this
        if (this is Error) throw this
        val exception = this as? Exception ?: Exception(this)
        Log.e("TemplateViewModel", "Failed to load template with id: $templateId", exception)
        return AppError.Unknown(exception).toUiState()
    }
}
