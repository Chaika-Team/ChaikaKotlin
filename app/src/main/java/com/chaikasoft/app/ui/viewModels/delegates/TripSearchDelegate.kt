package com.chaikasoft.app.ui.viewModels.delegates

import android.util.Log
import com.chaikasoft.app.domain.common.AppError
import com.chaikasoft.app.domain.models.trip.StationDomain
import com.chaikasoft.app.domain.sealed.SearchTripsResult
import com.chaikasoft.app.domain.usecases.SearchTripsByStationsUseCase
import com.chaikasoft.app.ui.mappers.AppErrorUiMapper
import com.chaikasoft.app.ui.state.TripsSearchUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TripSearchDelegate(
    private val searchTripsUseCase: SearchTripsByStationsUseCase,
    private val scope: CoroutineScope
) {

    private val _state = MutableStateFlow<TripsSearchUiState>(TripsSearchUiState.Idle)
    val state: StateFlow<TripsSearchUiState> = _state.asStateFlow()

    private val _searchDate = MutableStateFlow("")
    val searchDate: StateFlow<String> = _searchDate.asStateFlow()

    private val _startStation = MutableStateFlow<StationDomain?>(null)
    val startStation: StateFlow<StationDomain?> = _startStation.asStateFlow()

    private val _finishStation = MutableStateFlow<StationDomain?>(null)
    val finishStation: StateFlow<StationDomain?> = _finishStation.asStateFlow()

    private data class Params(val date: String, val from: String, val to: String)

    private var lastParams: Params? = null
    private var searchJob: Job? = null

    fun onDateChanged(value: String) { _searchDate.value = value }
    fun onStartStationChanged(station: StationDomain?) { _startStation.value = station }
    fun onFinishStationChanged(station: StationDomain?) { _finishStation.value = station }

    fun search(date: String, from: String, to: String) {
        val params = Params(date, from, to)
        if (hasCachedResult(params)) return

        lastParams = params
        searchJob?.cancel()
        searchJob = scope.launch {
            _state.value = TripsSearchUiState.Loading
            try {
                when (val result = searchTripsUseCase(date, from, to)) {
                    is SearchTripsResult.Success -> {
                        _state.value =
                            if (result.trips.isEmpty()) TripsSearchUiState.Empty
                            else TripsSearchUiState.Content(result.trips)
                    }
                    is SearchTripsResult.Failure -> {
                        val ui = AppErrorUiMapper.map(result.error)
                        _state.value = TripsSearchUiState.Error(ui.messageRes, ui.retryable)
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error", e)
                val ui = AppErrorUiMapper.map(AppError.Unknown(e))
                _state.value = TripsSearchUiState.Error(ui.messageRes, ui.retryable)
            }
        }
    }

    fun retry() { lastParams?.let { search(it.date, it.from, it.to) } }

    fun resetResults() {
        searchJob?.cancel()
        searchJob = null
        lastParams = null
        _state.value = TripsSearchUiState.Idle
    }

    fun resetAll() {
        resetResults()
        _searchDate.value = ""
        _startStation.value = null
        _finishStation.value = null
    }

    private fun hasCachedResult(params: Params): Boolean {
        if (lastParams != params) return false
        return _state.value is TripsSearchUiState.Content
                || _state.value is TripsSearchUiState.Empty
    }

    private companion object { const val TAG = "TripSearchDelegate" }
}