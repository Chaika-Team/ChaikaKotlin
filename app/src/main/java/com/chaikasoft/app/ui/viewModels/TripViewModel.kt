package com.chaikasoft.app.ui.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaikasoft.app.domain.models.trip.CarriageDomain
import com.chaikasoft.app.domain.models.trip.StationDomain
import com.chaikasoft.app.domain.models.trip.TripDomain
import com.chaikasoft.app.domain.usecases.CompleteShiftAndSendUseCase
import com.chaikasoft.app.domain.usecases.GetActiveShiftUseCase
import com.chaikasoft.app.domain.usecases.GetPagedStationSuggestionsUseCase
import com.chaikasoft.app.domain.usecases.GetShiftHistoryUseCase
import com.chaikasoft.app.domain.usecases.SearchTripsByStationsUseCase
import com.chaikasoft.app.domain.usecases.SendShiftReportUseCase
import com.chaikasoft.app.domain.usecases.StartShiftUseCase
import com.chaikasoft.app.ui.viewModels.delegates.CarriageInputDelegate
import com.chaikasoft.app.ui.viewModels.delegates.HistoryDelegate
import com.chaikasoft.app.ui.viewModels.delegates.ShiftDelegate
import com.chaikasoft.app.ui.viewModels.delegates.StationSuggestionsDelegate
import com.chaikasoft.app.ui.viewModels.delegates.TripSearchDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripViewModel @Inject constructor(
    searchTripsUseCase: SearchTripsByStationsUseCase,
    pagedSuggestionsUseCase: GetPagedStationSuggestionsUseCase,
    startShiftUseCase: StartShiftUseCase,
    getActiveShiftUseCase: GetActiveShiftUseCase,
    completeShiftUseCase: CompleteShiftAndSendUseCase,
    getShiftHistoryUseCase: GetShiftHistoryUseCase,
    sendShiftReportUseCase: SendShiftReportUseCase
) : ViewModel() {

    /* -------- delegates -------- */

    private val suggestions = StationSuggestionsDelegate(pagedSuggestionsUseCase, viewModelScope)

    private val tripSearch = TripSearchDelegate(searchTripsUseCase, viewModelScope)

    private val shift = ShiftDelegate(
        startShiftUseCase, getActiveShiftUseCase, completeShiftUseCase, viewModelScope
    )

    private val carriageInput = CarriageInputDelegate(viewModelScope)

    private val history = HistoryDelegate(
        getShiftHistoryUseCase = getShiftHistoryUseCase,
        sendShiftReportUseCase = sendShiftReportUseCase,
        scope = viewModelScope
    )

    /* -------- facade: trip search -------- */

    val searchDate = tripSearch.searchDate
    val searchStartStation = tripSearch.startStation
    val searchFinishStation = tripSearch.finishStation
    val tripsSearchState = tripSearch.state

    val fromQuery = suggestions.fromQuery
    val toQuery = suggestions.toQuery
    val fromSuggestions = suggestions.fromSuggestions
    val toSuggestions = suggestions.toSuggestions

    fun onSearchDateChanged(value: String) = tripSearch.onDateChanged(value)
    fun onFromQueryChanged(text: String) = suggestions.onFromQueryChanged(text)
    fun onToQueryChanged(text: String) = suggestions.onToQueryChanged(text)

    fun getTrips(searchDate: String, searchStart: String, searchFinish: String) {
        tripSearch.search(searchDate, searchStart, searchFinish)
    }

    fun retryTrips() = tripSearch.retry()
    fun resetTripsSearchResults() = tripSearch.resetResults()

    /* -------- facade: shift and carriage -------- */

    val selectedTripRecord = shift.selectedTrip
    val finishTripDialog = shift.finishDialog

    val carriageNumber = carriageInput.number
    val isCarriageInputValid = carriageInput.isValid

    fun onCarriageNumberChanged(raw: String) = carriageInput.onNumberChanged(raw)
    fun dismissFinishTripDialog() = shift.dismissFinishDialog()
    fun checkActiveShift() = shift.checkActiveShift()
    fun finishCurrentTrip() = shift.finishCurrentTrip()

    fun selectTrip(trip: TripDomain) {
        shift.selectTrip(trip)
        carriageInput.reset()
    }

    /* -------- facade: history and retry -------- */

    val shiftHistory = history.trips

    val retryConfirm = history.retryConfirm
    val retryResult = history.retryResult

    fun loadHistory() = history.startObserving()
    fun stopHistoryObserving() = history.stopObserving()
    fun requestRetrySend(uuid: String) = history.requestRetrySend(uuid)
    fun confirmRetrySend() = history.confirmRetrySend()
    fun dismissRetryConfirm() = history.dismissRetryConfirm()
    fun dismissRetryResult() = history.dismissRetryResult()

    /* -------- navigation flag -------- */

    private var preserveSearchOnNextShow = false

    fun preserveSearchForBackNavigation() {
        preserveSearchOnNextShow = true
    }

    fun onFindByNumberScreenShown() {
        if (preserveSearchOnNextShow) {
            preserveSearchOnNextShow = false
            return
        }
        tripSearch.resetAll()
        suggestions.reset()
    }

    /* -------- delegates coordination -------- */

    fun onStartStationChanged(station: StationDomain?) {
        tripSearch.onStartStationChanged(station)
        suggestions.onFromQueryChanged(station?.name.orEmpty())
    }

    fun onFinishStationChanged(station: StationDomain?) {
        tripSearch.onFinishStationChanged(station)
        suggestions.onToQueryChanged(station?.name.orEmpty())
    }

    fun confirmCarriageInput(onSuccess: () -> Unit) {
        val trip = shift.selectedTrip.value ?: return
        val number = carriageInput.validatedNumber ?: run {
            Log.e(TAG, "Invalid carriage number: ${carriageInput.number.value}")
            return
        }

        viewModelScope.launch {
            try {
                val carriage = CarriageDomain(
                    carNumber = number.toString(),
                    classType = ""
                )
                shift.startShift(trip, carriage)
                shift.checkActiveShift()
                onSuccess()
            } catch (e: Exception) {
                Log.e(TAG, "Error confirming carriage input", e)
            }
        }
    }

    private companion object { const val TAG = "TripViewModel" }
}
