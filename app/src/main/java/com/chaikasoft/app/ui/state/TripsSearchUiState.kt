package com.chaikasoft.app.ui.state

import androidx.annotation.StringRes
import com.chaikasoft.app.domain.models.trip.TripDomain

sealed interface TripsSearchUiState {
    data object Idle : TripsSearchUiState
    data object Loading : TripsSearchUiState
    data class Content(val trips: List<TripDomain>) : TripsSearchUiState
    data object Empty : TripsSearchUiState
    data class Error(
        @StringRes val messageRes: Int,
        val retryable: Boolean
    ) : TripsSearchUiState
}