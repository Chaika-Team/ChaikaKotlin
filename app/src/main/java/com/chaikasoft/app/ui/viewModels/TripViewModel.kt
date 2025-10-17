package com.chaikasoft.app.ui.viewModels

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.chaikasoft.app.domain.models.trip.CarriageDomain
import com.chaikasoft.app.domain.models.trip.ConductorTripShiftDomain
import com.chaikasoft.app.domain.models.trip.StationDomain
import com.chaikasoft.app.domain.models.trip.TripDomain
import com.chaikasoft.app.domain.sealed.SendReportResult
import com.chaikasoft.app.domain.usecases.CompleteShiftAndSendUseCase
import com.chaikasoft.app.domain.usecases.GetActiveShiftUseCase
import com.chaikasoft.app.domain.usecases.GetCarriagesForTrainUseCase
import com.chaikasoft.app.domain.usecases.GetPagedStationSuggestionsUseCase
import com.chaikasoft.app.domain.usecases.SearchTripsByStationsUseCase
import com.chaikasoft.app.domain.usecases.StartShiftUseCase
import com.chaikasoft.app.ui.viewModels.tripMocks.fetchAndSaveHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.chaikasoft.app.R

@HiltViewModel
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class TripViewModel @Inject constructor(
    private val getCarriagesForTrainUseCase: GetCarriagesForTrainUseCase,
    private val searchTripByStationUseCase: SearchTripsByStationsUseCase,
    private val getPagedStationSuggestions: GetPagedStationSuggestionsUseCase,
    private val startShiftUseCase: StartShiftUseCase,
    private val getActiveShiftUseCase: GetActiveShiftUseCase,
    private val completeShiftUseCase: CompleteShiftAndSendUseCase
) : ViewModel() {

    // --- New: query states for FROM/TO ---
    private val _fromQuery = MutableStateFlow("")
    private val _toQuery = MutableStateFlow("")

    val fromSuggestions: Flow<PagingData<StationDomain>> =
        _fromQuery
            .map { it.trim() }
            .filter { it.length >= 2 }
            .debounce(500)
            .distinctUntilChanged()
            .flatMapLatest { query -> getPagedStationSuggestions(query, pageSize = 20) }
            .cachedIn(viewModelScope)

    val toSuggestions: Flow<PagingData<StationDomain>> =
        _toQuery
            .map { it.trim() }
            .filter { it.length >= 2 }
            .debounce(500)
            .distinctUntilChanged()
            .flatMapLatest { query -> getPagedStationSuggestions(query, pageSize = 20) }
            .cachedIn(viewModelScope)

    fun onFromQueryChanged(text: String) { _fromQuery.value = text }
    fun onToQueryChanged(text: String) { _toQuery.value = text }

    private val _selectedTripRecord = MutableStateFlow<TripDomain?>(null)
    val selectedTripRecord: StateFlow<TripDomain?> = _selectedTripRecord.asStateFlow()

    private val _selectedCarriage = MutableStateFlow<CarriageDomain?>(null)

    private val _shiftStatus = MutableStateFlow<Boolean?>(null)
    val shiftStatus: StateFlow<Boolean?> = _shiftStatus.asStateFlow()

    private val _activeShift = MutableStateFlow<ConductorTripShiftDomain?>(null)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _pagingHistoryFlow = MutableStateFlow<List<TripDomain>>(emptyList())
    val pagingHistoryFlow: StateFlow<List<TripDomain>> = _pagingHistoryFlow.asStateFlow()

    private val _foundTripsList = MutableStateFlow<List<TripDomain>>(emptyList())
    val foundTripsList: StateFlow<List<TripDomain>> = _foundTripsList.asStateFlow()

    private val _carriageList = MutableStateFlow<List<CarriageDomain>>(emptyList())
    val carriageList: StateFlow<List<CarriageDomain>> = _carriageList.asStateFlow()

    data class FinishTripDialog(@StringRes val messageRes: Int)

    private val _finishTripDialog = MutableStateFlow<FinishTripDialog?>(null)
    val finishTripDialog = _finishTripDialog.asStateFlow()

    fun dismissFinishTripDialog() {
        _finishTripDialog.value = null
    }

    /**
     * Отфильтрованный список вагонов с валидным номером (число)
     * + подробное логирование.
     */
    val validCarriages: StateFlow<List<CarriageDomain>> =
        carriageList
            .map { original ->
                val filtered = original.filter { it.carNumber.toIntOrNull() != null }
                Log.d(
                    "TripViewModel",
                    "Carriages filter -> input=${original.size}, valid=${filtered.size}"
                )
                filtered
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    /**
     * Сгруппированные по классу вагоны.
     * Ключ всегда String (используем toString() на случай enum'ов и пр.)
     * + логирование распределения по группам.
     */
    val groupedCarriages: StateFlow<Map<String, List<CarriageDomain>>> =
        validCarriages
            .map { list ->
                val grouped = list.groupBy { it.classType.toString() }
                val summary = grouped.entries.joinToString { "${it.key}:${it.value.size}" }
                Log.d(
                    "TripViewModel",
                    "Carriages group -> groups=${grouped.size} [$summary]"
                )
                grouped
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyMap()
            )

    fun loadHistory() {
        loadHistoryData()
    }

    fun finishCurrentTrip() = viewModelScope.launch {
        val trip = _selectedTripRecord.value ?: return@launch
        _selectedTripRecord.value = null
        _selectedCarriage.value = null
        try {
            when (completeShiftUseCase(trip.uuid)) {
                is SendReportResult.Success ->
                    _finishTripDialog.value = FinishTripDialog(R.string.trip_finish_success)
                is SendReportResult.AlreadySent ->
                    _finishTripDialog.value = FinishTripDialog(R.string.trip_finish_already_sent)
                is SendReportResult.MissingReport ->
                    _finishTripDialog.value = FinishTripDialog(R.string.trip_finish_missing_report)
                is SendReportResult.TemporaryFailure ->
                    _finishTripDialog.value = FinishTripDialog(R.string.trip_finish_temp_failure)
                is SendReportResult.PermanentFailure ->
                    _finishTripDialog.value = FinishTripDialog(R.string.trip_finish_perm_failure)
            }
        } catch (e: Exception) {
            Log.e("TripViewModel", "Error finishing trip", e)
            _finishTripDialog.value = FinishTripDialog(R.string.trip_finish_temp_failure)
        }
    }

    fun setSelectCarriage(tripRecord: TripDomain) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _selectedTripRecord.value = tripRecord
                val list = getCarriagesForTrainUseCase(tripRecord.uuid)
                _carriageList.value = list
                Log.d(
                    "TripViewModel",
                    "Loaded ${list.size} carriages for trip=${tripRecord.uuid}"
                )
            } catch (e: Exception) {
                Log.e("TripViewModel", "Error loading carriages", e)
                _carriageList.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setCurrentTrip(carriage: CarriageDomain) {
        viewModelScope.launch {
            _selectedCarriage.value = carriage
            startShift()
            checkActiveShift()
        }
    }

    private suspend fun startShift() {
        val trip = _selectedTripRecord.value
        val carriage = _selectedCarriage.value

        if (trip == null || carriage == null) {
            _shiftStatus.value = false
            return
        }

        try {
            val success = startShiftUseCase(trip, carriage)
            _shiftStatus.value = success
            if (success) {
                Log.d("TripViewModel", "Shift started successfully")
            } else {
                Log.d("TripViewModel", "Failed to start shift")
            }
        } catch (e: Exception) {
            _shiftStatus.value = false
            Log.e("TripViewModel", "Failed to start shift with error", e)
        }
    }

    fun checkActiveShift() {
        viewModelScope.launch {
            Log.d("TripViewModel", "Starting checkActiveShift")
            try {
                val shift = getActiveShiftUseCase().first()
                _activeShift.value = shift
                if (shift == null) {
                    _selectedTripRecord.update { null }
                    _selectedCarriage.update { null }
                    _shiftStatus.update { false }
                    Log.d("TripViewModel", "No active shift")
                } else {
                    _selectedTripRecord.update { shift.trip }
                    _selectedCarriage.update { shift.activeCarriage }
                    _shiftStatus.update { true }
                    Log.d("TripViewModel", "Exists active shift")
                }
            } catch (e: Exception) {
                Log.e("TripViewModel", "Failed to check active shift", e)
            }
        }
    }

    fun getSelectedTrip(): TripDomain? = _selectedTripRecord.value

    fun loadHistoryData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                fetchAndSaveHistoryUseCase()
                getHistory()
            } catch (e: Exception) {
                Log.e("TripViewModel", "Failed to load history", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun getHistory() {
        viewModelScope.launch { _pagingHistoryFlow.value = fetchAndSaveHistoryUseCase() }
    }

    fun getTrips(searchDate: String, searchStart: Int, searchFinish: Int) {
        viewModelScope.launch {
            _foundTripsList.value = searchTripByStationUseCase(searchDate, searchStart, searchFinish)
        }
    }
}
