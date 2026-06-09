package com.chaikasoft.app.ui.state

import androidx.annotation.StringRes
import com.chaikasoft.app.domain.models.ResolvedTemplateDetailDomain

sealed interface TemplateDetailUiState {
    data object Idle : TemplateDetailUiState
    data object Loading : TemplateDetailUiState
    data class Content(val detail: ResolvedTemplateDetailDomain) : TemplateDetailUiState
    data class Error(@StringRes val messageRes: Int, val retryable: Boolean) :
        TemplateDetailUiState
}
