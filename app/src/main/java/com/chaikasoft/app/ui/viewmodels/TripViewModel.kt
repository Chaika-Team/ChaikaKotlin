package com.chaikasoft.app.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaikasoft.app.R
import com.chaikasoft.app.domain.models.trip.CarriageDomain
import com.chaikasoft.app.domain.models.trip.StationDomain
import com.chaikasoft.app.domain.models.trip.TripDomain
import com.chaikasoft.app.domain.sealed.StartShiftResult
import com.chaikasoft.app.domain.usecases.CompleteShiftAndSendUseCase
import com.chaikasoft.app.domain.usecases.DeleteActiveShiftUseCase
import com.chaikasoft.app.domain.usecases.GetActiveShiftUseCase
import com.chaikasoft.app.domain.usecases.GetPagedStationSuggestionsUseCase
import com.chaikasoft.app.domain.usecases.GetShiftHistoryUseCase
import com.chaikasoft.app.domain.usecases.HasAnyPackageItemsOnceUseCase
import com.chaikasoft.app.domain.usecases.SearchTripsByStationsUseCase
import com.chaikasoft.app.domain.usecases.SendShiftReportUseCase
import com.chaikasoft.app.domain.usecases.StartShiftUseCase
import com.chaikasoft.app.ui.viewmodels.delegates.CarriageInputDelegate
import com.chaikasoft.app.ui.viewmodels.delegates.HistoryDelegate
import com.chaikasoft.app.ui.viewmodels.delegates.ShiftDelegate
import com.chaikasoft.app.ui.viewmodels.delegates.StationSuggestionsDelegate
import com.chaikasoft.app.ui.viewmodels.delegates.TripSearchDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class TripViewModel @Inject constructor(
    searchTripsUseCase: SearchTripsByStationsUseCase,
    pagedSuggestionsUseCase: GetPagedStationSuggestionsUseCase,
    startShiftUseCase: StartShiftUseCase,
    getActiveShiftUseCase: GetActiveShiftUseCase,
    completeShiftUseCase: CompleteShiftAndSendUseCase,
    deleteActiveShiftUseCase: DeleteActiveShiftUseCase,
    hasAnyPackageItemsOnceUseCase: HasAnyPackageItemsOnceUseCase,
    getShiftHistoryUseCase: GetShiftHistoryUseCase,
    sendShiftReportUseCase: SendShiftReportUseCase
) : ViewModel() {

    /* -------- delegates -------- */

    private val suggestions = StationSuggestionsDelegate(pagedSuggestionsUseCase, viewModelScope)

    private val tripSearch = TripSearchDelegate(searchTripsUseCase, viewModelScope)

    private val shift = ShiftDelegate(
        startShiftUseCase,
        getActiveShiftUseCase,
        completeShiftUseCase,
        deleteActiveShiftUseCase,
        hasAnyPackageItemsOnceUseCase,
        viewModelScope
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

    val selectedTripForCreation = shift.selectedTripForCreation
    val activeTripRecord = shift.activeTrip
    val isFinishingTrip = shift.isFinishing
    val finishTripDialog = shift.finishDialog
    val deleteTripDialog = shift.deleteDialog

    val carriageNumber = carriageInput.number
    val isCarriageInputValid = carriageInput.isValid
    private val _startShiftErrorMessageRes = MutableStateFlow<Int?>(null)
    val startShiftErrorMessageRes: StateFlow<Int?> = _startShiftErrorMessageRes.asStateFlow()

    fun onCarriageNumberChanged(raw: String) = carriageInput.onNumberChanged(raw)
    fun dismissFinishTripDialog() = shift.dismissFinishDialog()
    fun finishCurrentTrip() = shift.finishCurrentTrip()
    fun requestDeleteCurrentTrip() = shift.requestDeleteCurrentTrip()
    fun onPreservePackageChanged(preservePackage: Boolean) =
        shift.onPreservePackageChanged(preservePackage)
    fun confirmDeleteCurrentTrip() = shift.confirmDeleteCurrentTrip()
    fun dismissDeleteTripDialog() = shift.dismissDeleteDialog()

    fun selectTrip(trip: TripDomain) {
        shift.selectTrip(trip)
        carriageInput.reset()
        _startShiftErrorMessageRes.value = null
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

    @Suppress("TooGenericExceptionCaught")
    fun confirmCarriageInput(onSuccess: () -> Unit) {
        val trip = shift.selectedTripForCreation.value ?: return
        val number = carriageInput.validatedNumber ?: run {
            Log.e(TAG, "Invalid carriage number: ${carriageInput.number.value}")
            return
        }

        _startShiftErrorMessageRes.value = null
        viewModelScope.launch {
            try {
                val carriage = CarriageDomain(
                    carNumber = number.toString(),
                    classType = ""
                )
                when (shift.startShift(trip, carriage)) {
                    StartShiftResult.Started,
                    StartShiftResult.ActiveShiftAlreadyExists -> {
                        shift.clearSelectedTripForCreation()
                        onSuccess()
                    }
                    StartShiftResult.TripAlreadyRegistered ->
                        _startShiftErrorMessageRes.value = R.string.trip_already_registered
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                Log.e(TAG, "Error confirming carriage input", e)
                _startShiftErrorMessageRes.value = R.string.start_shift_failed
            } catch (e: RuntimeException) {
                Log.e(TAG, "Error confirming carriage input", e)
                _startShiftErrorMessageRes.value = R.string.start_shift_failed
            }
        }
    }

    private companion object {
        const val TAG = "TripViewModel"
    }
}
