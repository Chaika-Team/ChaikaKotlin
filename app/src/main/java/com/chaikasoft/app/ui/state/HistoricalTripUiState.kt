package com.chaikasoft.app.ui.state

import androidx.annotation.StringRes
import com.chaikasoft.app.domain.models.HistoricalTripSnapshot

sealed interface HistoricalTripUiState {
    data object Loading : HistoricalTripUiState

    data class Content(val snapshot: HistoricalTripSnapshot) : HistoricalTripUiState

    data class Error(@StringRes val messageRes: Int) : HistoricalTripUiState
}
