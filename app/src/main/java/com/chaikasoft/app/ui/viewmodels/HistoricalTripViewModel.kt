package com.chaikasoft.app.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaikasoft.app.R
import com.chaikasoft.app.domain.usecases.GetHistoricalTripSnapshotUseCase
import com.chaikasoft.app.ui.navigation.Routes
import com.chaikasoft.app.ui.state.HistoricalTripUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class HistoricalTripViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getHistoricalTripSnapshot: GetHistoricalTripSnapshotUseCase
) : ViewModel() {
    private val shiftUuid: String = checkNotNull(savedStateHandle[Routes.ARG_SHIFT_UUID])

    private val _uiState = MutableStateFlow<HistoricalTripUiState>(HistoricalTripUiState.Loading)
    val uiState: StateFlow<HistoricalTripUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = HistoricalTripUiState.Loading
            _uiState.value = try {
                HistoricalTripUiState.Content(getHistoricalTripSnapshot(shiftUuid))
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                HistoricalTripUiState.Error(R.string.trip_finish_missing_report)
            }
        }
    }
}
