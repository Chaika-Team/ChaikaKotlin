package com.chaikasoft.app.ui.viewModels.delegates

import android.util.Log
import androidx.annotation.StringRes
import com.chaikasoft.app.R
import com.chaikasoft.app.domain.models.trip.CarriageDomain
import com.chaikasoft.app.domain.models.trip.ConductorTripShiftDomain
import com.chaikasoft.app.domain.models.trip.TripDomain
import com.chaikasoft.app.domain.sealed.SendReportResult
import com.chaikasoft.app.domain.usecases.CompleteShiftAndSendUseCase
import com.chaikasoft.app.domain.usecases.GetActiveShiftUseCase
import com.chaikasoft.app.domain.usecases.StartShiftUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.IOException

class ShiftDelegate(
    private val startShiftUseCase: StartShiftUseCase,
    private val getActiveShiftUseCase: GetActiveShiftUseCase,
    private val completeShiftUseCase: CompleteShiftAndSendUseCase,
    private val scope: CoroutineScope
) {

    data class FinishTripDialog(@StringRes val messageRes: Int)

    private val _selectedTrip = MutableStateFlow<TripDomain?>(null)
    val selectedTrip: StateFlow<TripDomain?> = _selectedTrip.asStateFlow()

    private val _selectedCarriage = MutableStateFlow<CarriageDomain?>(null)

    private val _shiftStatus = MutableStateFlow<Boolean?>(null)
    val shiftStatus: StateFlow<Boolean?> = _shiftStatus.asStateFlow()

    private val _activeShift = MutableStateFlow<ConductorTripShiftDomain?>(null)

    private val _finishDialog = MutableStateFlow<FinishTripDialog?>(null)
    val finishDialog: StateFlow<FinishTripDialog?> = _finishDialog.asStateFlow()

    fun selectTrip(trip: TripDomain) {
        _selectedTrip.value = trip
        Log.d(TAG, "Selected trip: ${trip.uuid}")
    }

    suspend fun startShift(trip: TripDomain, carriage: CarriageDomain): Boolean {
        return try {
            _selectedCarriage.value = carriage
            val ok = startShiftUseCase(trip, carriage)
            _shiftStatus.value = ok
            Log.d(TAG, if (ok) "Shift started" else "Shift start failed")
            ok
        } catch (e: IllegalArgumentException) {
            _shiftStatus.value = false
            Log.e(TAG, "Failed to start shift", e)
            false
        }
    }

    fun checkActiveShift() {
        scope.launch {
            try {
                val shift = getActiveShiftUseCase().first()
                _activeShift.value = shift
                if (shift == null) {
                    _selectedTrip.value = null
                    _selectedCarriage.value = null
                    _shiftStatus.value = false
                } else {
                    _selectedTrip.value = shift.trip
                    _selectedCarriage.value = shift.activeCarriage
                    _shiftStatus.value = true
                }
                Log.d(TAG, if (shift != null) "Active shift found" else "No active shift")
            } catch (e: IOException) {
                Log.e(TAG, "Failed to check active shift", e)
            }
        }
    }

    fun finishCurrentTrip() {
        val trip = _selectedTrip.value ?: return
        _selectedTrip.value = null
        _selectedCarriage.value = null

        scope.launch {
            try {
                val msg = when (completeShiftUseCase(trip.uuid)) {
                    is SendReportResult.Success          -> R.string.trip_finish_success
                    is SendReportResult.AlreadySent      -> R.string.trip_finish_already_sent
                    is SendReportResult.MissingReport    -> R.string.trip_finish_missing_report
                    is SendReportResult.TemporaryFailure -> R.string.trip_finish_temp_failure
                    is SendReportResult.PermanentFailure -> R.string.trip_finish_perm_failure
                }
                _finishDialog.value = FinishTripDialog(msg)
            } catch (e: IOException) {
                Log.e(TAG, "Error finishing trip", e)
                _finishDialog.value = FinishTripDialog(R.string.trip_finish_temp_failure)
            }
        }
    }

    fun dismissFinishDialog() { _finishDialog.value = null }

    private companion object { const val TAG = "ShiftDelegate" }
}