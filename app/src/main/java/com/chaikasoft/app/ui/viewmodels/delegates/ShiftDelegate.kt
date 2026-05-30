package com.chaikasoft.app.ui.viewmodels.delegates

import android.util.Log
import androidx.annotation.StringRes
import com.chaikasoft.app.R
import com.chaikasoft.app.domain.models.trip.CarriageDomain
import com.chaikasoft.app.domain.models.trip.ConductorTripShiftDomain
import com.chaikasoft.app.domain.models.trip.TripDomain
import com.chaikasoft.app.domain.sealed.SendReportResult
import com.chaikasoft.app.domain.sealed.StartShiftResult
import com.chaikasoft.app.domain.usecases.CompleteShiftAndSendUseCase
import com.chaikasoft.app.domain.usecases.DeleteActiveShiftUseCase
import com.chaikasoft.app.domain.usecases.GetActiveShiftUseCase
import com.chaikasoft.app.domain.usecases.HasAnyPackageItemsOnceUseCase
import com.chaikasoft.app.domain.usecases.StartShiftUseCase
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ShiftDelegate(
    private val startShiftUseCase: StartShiftUseCase,
    private val getActiveShiftUseCase: GetActiveShiftUseCase,
    private val completeShiftUseCase: CompleteShiftAndSendUseCase,
    private val deleteActiveShiftUseCase: DeleteActiveShiftUseCase,
    private val hasAnyPackageItemsOnceUseCase: HasAnyPackageItemsOnceUseCase,
    private val scope: CoroutineScope
) {

    data class FinishTripDialog(@StringRes val messageRes: Int)

    data class DeleteTripDialog(
        val hasPackageItems: Boolean? = null,
        val preservePackage: Boolean = true,
        val isDeleting: Boolean = false,
        @StringRes val errorMessageRes: Int? = null
    )

    private val _selectedTripForCreation = MutableStateFlow<TripDomain?>(null)
    val selectedTripForCreation: StateFlow<TripDomain?> = _selectedTripForCreation.asStateFlow()

    private val activeShift: StateFlow<ConductorTripShiftDomain?> =
        getActiveShiftUseCase()
            .distinctUntilChanged()
            .catch { e ->
                Log.e(TAG, "Failed to observe active shift", e)
                emit(null)
            }
            .stateIn(scope, SharingStarted.Eagerly, null)
    val activeTrip: StateFlow<TripDomain?> =
        activeShift
            .map { shift -> shift?.trip }
            .distinctUntilChanged()
            .stateIn(scope, SharingStarted.Eagerly, null)

    private val _finishDialog = MutableStateFlow<FinishTripDialog?>(null)
    val finishDialog: StateFlow<FinishTripDialog?> = _finishDialog.asStateFlow()
    private val _isFinishing = MutableStateFlow(false)
    val isFinishing: StateFlow<Boolean> = _isFinishing.asStateFlow()

    private val _deleteDialog = MutableStateFlow<DeleteTripDialog?>(null)
    val deleteDialog: StateFlow<DeleteTripDialog?> = _deleteDialog.asStateFlow()
    private var packageInspectionJob: Job? = null

    fun selectTrip(trip: TripDomain) {
        _selectedTripForCreation.value = trip
        Log.d(TAG, "Selected trip: ${trip.uuid}")
    }

    fun clearSelectedTripForCreation() {
        _selectedTripForCreation.value = null
    }

    suspend fun startShift(trip: TripDomain, carriage: CarriageDomain): StartShiftResult {
        val result = startShiftUseCase(trip, carriage)
        Log.d(TAG, "Shift start result: $result")
        return result
    }

    @Suppress("TooGenericExceptionCaught")
    fun finishCurrentTrip() {
        val trip = activeTrip.value ?: return
        if (_isFinishing.value || _deleteDialog.value != null) return

        _isFinishing.value = true
        scope.launch {
            try {
                val msg = when (completeShiftUseCase(trip.uuid)) {
                    is SendReportResult.Success -> R.string.trip_finish_success
                    is SendReportResult.AlreadySent -> R.string.trip_finish_already_sent
                    is SendReportResult.MissingReport -> R.string.trip_finish_missing_report
                    is SendReportResult.TemporaryFailure -> R.string.trip_finish_temp_failure
                    is SendReportResult.PermanentFailure -> R.string.trip_finish_perm_failure
                }
                _finishDialog.value = FinishTripDialog(msg)
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                Log.e(TAG, "Error finishing trip", e)
                _finishDialog.value = FinishTripDialog(R.string.trip_finish_temp_failure)
            } catch (e: RuntimeException) {
                Log.e(TAG, "Error finishing trip", e)
                _finishDialog.value = FinishTripDialog(R.string.trip_finish_temp_failure)
            } finally {
                _isFinishing.value = false
            }
        }
    }

    fun dismissFinishDialog() {
        _finishDialog.value = null
    }

    @Suppress("TooGenericExceptionCaught")
    fun requestDeleteCurrentTrip() {
        if (activeTrip.value == null || _isFinishing.value || _deleteDialog.value != null) return

        val pendingDialog = DeleteTripDialog()
        _deleteDialog.value = pendingDialog
        packageInspectionJob?.cancel()
        packageInspectionJob = scope.launch {
            try {
                val hasPackageItems = hasAnyPackageItemsOnceUseCase()
                if (_deleteDialog.value === pendingDialog) {
                    _deleteDialog.value = pendingDialog.copy(hasPackageItems = hasPackageItems)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: RuntimeException) {
                Log.e(TAG, "Failed to inspect package before deleting shift", e)
                if (_deleteDialog.value === pendingDialog) {
                    _deleteDialog.value = pendingDialog.copy(
                        errorMessageRes = R.string.trip_delete_failure
                    )
                }
            }
        }
    }

    fun onPreservePackageChanged(preservePackage: Boolean) {
        val dialog = _deleteDialog.value ?: return
        if (dialog.hasPackageItems != true || dialog.isDeleting) return
        _deleteDialog.value = dialog.copy(preservePackage = preservePackage)
    }

    @Suppress("TooGenericExceptionCaught")
    fun confirmDeleteCurrentTrip() {
        val trip = activeTrip.value ?: return
        val dialog = _deleteDialog.value ?: return
        val hasPackageItems = dialog.hasPackageItems ?: return
        if (_isFinishing.value || dialog.isDeleting) return

        _deleteDialog.value = dialog.copy(isDeleting = true, errorMessageRes = null)
        scope.launch {
            try {
                deleteActiveShiftUseCase(
                    uuid = trip.uuid,
                    preservePackage = hasPackageItems && dialog.preservePackage
                )
                _deleteDialog.value = null
            } catch (e: CancellationException) {
                throw e
            } catch (e: RuntimeException) {
                Log.e(TAG, "Failed to delete shift: ${trip.uuid}", e)
                _deleteDialog.value = dialog.copy(
                    isDeleting = false,
                    errorMessageRes = R.string.trip_delete_failure
                )
            }
        }
    }

    fun dismissDeleteDialog() {
        if (_deleteDialog.value?.isDeleting == true) return
        packageInspectionJob?.cancel()
        packageInspectionJob = null
        _deleteDialog.value = null
    }

    private companion object {
        const val TAG = "ShiftDelegate"
    }
}
