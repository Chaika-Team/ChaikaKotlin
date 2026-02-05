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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.chaikasoft.app.domain.sealed.SearchTripsResult
import com.chaikasoft.app.ui.mappers.AppErrorUiMapper
import com.chaikasoft.app.ui.state.TripsSearchUiState
import com.chaikasoft.app.domain.common.AppError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import javax.inject.Inject
import com.chaikasoft.app.R
import kotlinx.coroutines.flow.flowOf


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

    private val _fromQuery = MutableStateFlow("")
    private val _toQuery = MutableStateFlow("")

    // уже есть _fromQuery/_toQuery — просто открыл наружу
    val fromQuery: StateFlow<String> = _fromQuery.asStateFlow()
    val toQuery: StateFlow<String> = _toQuery.asStateFlow()

    val fromSuggestions: Flow<PagingData<StationDomain>> =
        _fromQuery
            .map { it.trim() }
            .debounce(500)
            .distinctUntilChanged()
            .flatMapLatest { query ->
                if (query.length < 2) flowOf(PagingData.empty())
                else getPagedStationSuggestions(query, pageSize = 20)
            }
            .cachedIn(viewModelScope)

    val toSuggestions: Flow<PagingData<StationDomain>> =
        _toQuery
            .map { it.trim() }
            .debounce(500)
            .distinctUntilChanged()
            .flatMapLatest { query ->
                if (query.length < 2) flowOf(PagingData.empty())
                else getPagedStationSuggestions(query, pageSize = 20)
            }
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

    // --- Trips search UI state (НОВЫЙ) ---
    private val _tripsSearchState = MutableStateFlow<TripsSearchUiState>(TripsSearchUiState.Idle)
    val tripsSearchState: StateFlow<TripsSearchUiState> = _tripsSearchState.asStateFlow()

    // Для retry (НОВЫЙ)
    private data class TripsSearchParams(
        val date: String,
        val from: String,
        val to: String
    )
    private var lastTripsSearchParams: TripsSearchParams? = null

    // Отменяем предыдущий поиск, чтобы не было гонок (НОВЫЙ)
    private var searchTripsJob: Job? = null

    // --- Search form state (single source of truth, перенесено из FindByNumberView.kt) ---
    private val _searchDate = MutableStateFlow("")
    val searchDate: StateFlow<String> = _searchDate.asStateFlow()

    private val _searchStartStation = MutableStateFlow<StationDomain?>(null)
    val searchStartStation: StateFlow<StationDomain?> = _searchStartStation.asStateFlow()

    private val _searchFinishStation = MutableStateFlow<StationDomain?>(null)
    val searchFinishStation: StateFlow<StationDomain?> = _searchFinishStation.asStateFlow()


    private val _carriageList = MutableStateFlow<List<CarriageDomain>>(emptyList())
    val carriageList: StateFlow<List<CarriageDomain>> = _carriageList.asStateFlow()

    data class FinishTripDialog(@StringRes val messageRes: Int)

    private val _finishTripDialog = MutableStateFlow<FinishTripDialog?>(null)
    val finishTripDialog = _finishTripDialog.asStateFlow()

    private var preserveSearchOnNextShow: Boolean = false

    /** Устанавливаю перед навигацией к вагонам */
    fun preserveSearchForBackNavigation() {
        preserveSearchOnNextShow = true
    }

    /** Вызываю только на входе в FindByNumberView */
    fun onFindByNumberScreenShown() {
        if (preserveSearchOnNextShow) {
            // Возврат со следующего экрана (вагоны) — сохраняем
            preserveSearchOnNextShow = false
            return
        }
        // Если обычный заход на экран — сброс формы и результатов
        resetTripsSearchAll()
    }

    private fun resetTripsSearchAll() {
        _searchDate.value = ""
        _searchStartStation.value = null
        _searchFinishStation.value = null
        _fromQuery.value = ""
        _toQuery.value = ""

        resetTripsSearchResults()
    }

    fun resetTripsSearchResults() {
        searchTripsJob?.cancel()
        searchTripsJob = null
        lastTripsSearchParams = null
        _tripsSearchState.value = TripsSearchUiState.Idle
    }

    /**
     * Предотвращение автопоиска, если есть валидные результаты по предыдущиму поиску.
     */
    private fun hasValidCachedResultFor(date: String, from: String, to: String): Boolean {
        val p = lastTripsSearchParams
        if (p == null || p.date != date || p.from != from || p.to != to) return false
        return when (_tripsSearchState.value) {
            is TripsSearchUiState.Content,
            TripsSearchUiState.Empty -> true
            else -> false
        }
    }

    fun dismissFinishTripDialog() {
        _finishTripDialog.value = null
    }

    fun onSearchDateChanged(value: String) { _searchDate.value = value }

    fun onStartStationChanged(station: StationDomain?) {
        _searchStartStation.value = station
        if (station != null) {
            _fromQuery.value = station.name
        } else {
            _fromQuery.value = ""
        }
    }

    fun onFinishStationChanged(station: StationDomain?) {
        _searchFinishStation.value = station
        if (station != null) {
            _toQuery.value = station.name
        } else {
            _toQuery.value = ""
        }
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

    fun getTrips(searchDate: String, searchStart: String, searchFinish: String) {
        val params = TripsSearchParams(searchDate, searchStart, searchFinish)
        if (hasValidCachedResultFor(params.date, params.from, params.to)) return

        lastTripsSearchParams = params

        searchTripsJob?.cancel()

        searchTripsJob = viewModelScope.launch {
            _tripsSearchState.value = TripsSearchUiState.Loading

            try {
                when (val result = searchTripByStationUseCase(params.date, params.from, params.to)) {
                    is SearchTripsResult.Success -> {
                        val trips = result.trips
                        _tripsSearchState.value =
                            if (trips.isEmpty()) TripsSearchUiState.Empty
                            else TripsSearchUiState.Content(trips)
                    }

                    is SearchTripsResult.Failure -> {
                        val uiError = AppErrorUiMapper.map(result.error)
                        _tripsSearchState.value = TripsSearchUiState.Error(
                            messageRes = uiError.messageRes,
                            retryable = uiError.retryable
                        )
                    }
                }
            } catch (e: CancellationException) {
                // Я сам отменил предыдущую корутину при новом поиске
                throw e
            } catch (e: Exception) {
                // Это только на случай багов. Сетевые ошибки сюда уже не попадают.
                Log.e("TripViewModel", "Unexpected error in getTrips()", e)
                val uiError = AppErrorUiMapper.map(AppError.Unknown(e))
                _tripsSearchState.value = TripsSearchUiState.Error(
                    messageRes = uiError.messageRes,
                    retryable = uiError.retryable
                )
            }
        }
    }

    fun retryTrips() {
        val p = lastTripsSearchParams ?: return
        getTrips(p.date, p.from, p.to)
    }

}
