package com.chaikasoft.app.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaikasoft.app.startup.TripGateStartupSeam
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class TripGateViewModel @Inject constructor(private val tripGateStartupSeam: TripGateStartupSeam) :
    ViewModel() {

    sealed interface TripGateUiState {
        data object Loading : TripGateUiState
        data class Ready(val hadRefreshFailure: Boolean) : TripGateUiState
    }

    private val _uiState = MutableStateFlow<TripGateUiState>(TripGateUiState.Loading)
    val uiState: StateFlow<TripGateUiState> = _uiState.asStateFlow()

    private var started = false

    fun prepare() {
        if (started) return
        started = true

        viewModelScope.launch {
            val hadRefreshFailure = try {
                tripGateStartupSeam.prepareForTripEntry().hadRefreshFailure
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Stations refresh crashed unexpectedly", e)
                true
            }

            _uiState.value = TripGateUiState.Ready(hadRefreshFailure = hadRefreshFailure)
        }
    }

    private companion object {
        const val TAG = "TripGateViewModel"
    }
}
