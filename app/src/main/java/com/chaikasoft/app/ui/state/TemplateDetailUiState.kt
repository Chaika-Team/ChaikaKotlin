package com.chaikasoft.app.ui.state

import com.chaikasoft.app.domain.models.TemplateDomain

sealed interface TemplateDetailUiState {
    data object Idle : TemplateDetailUiState
    data object Loading : TemplateDetailUiState
    data class Content(val template: TemplateDomain) : TemplateDetailUiState
    data class Error(val cause: Throwable) : TemplateDetailUiState
}
