package com.chaikasoft.app.ui.viewModels

import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import androidx.annotation.Dimension
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.chaikasoft.app.domain.models.trip.CarriageDomain
import com.chaikasoft.app.domain.models.trip.ConductorTripShiftDomain
import com.chaikasoft.app.domain.models.trip.StationDomain
import com.chaikasoft.app.domain.models.trip.TripDomain
import com.chaikasoft.app.domain.usecases.CompleteShiftUseCase
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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class TripViewModel @Inject constructor(
    private val getCarriagesForTrainUseCase: GetCarriagesForTrainUseCase,
    private val searchTripByStationUseCase: SearchTripsByStationsUseCase,
    private val getPagedStationSuggestions: GetPagedStationSuggestionsUseCase,
    private val startShiftUseCase: StartShiftUseCase,
    private val getActiveShiftUseCase: GetActiveShiftUseCase,
    private val completeShiftUseCase: CompleteShiftUseCase
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
    private val _selectedCarriage = MutableStateFlow<CarriageDomain?>(null)

    private val _shiftStatus = MutableStateFlow<Boolean?>(null)
    val shiftStatus: StateFlow<Boolean?> = _shiftStatus.asStateFlow()

    private val _activeShift = MutableStateFlow<ConductorTripShiftDomain?>(null)

    private val _uiState = MutableStateFlow<ScreenState>(ScreenState.NewTrip)
    val uiState: StateFlow<ScreenState> = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _pagingHistoryFlow = MutableStateFlow<List<TripDomain>>(emptyList())
    val pagingHistoryFlow: StateFlow<List<TripDomain>> = _pagingHistoryFlow.asStateFlow()

    private val _foundTripsList = MutableStateFlow<List<TripDomain>>(emptyList())
    val foundTripsList: StateFlow<List<TripDomain>> = _foundTripsList.asStateFlow()

    private val _carriageList = MutableStateFlow<List<CarriageDomain>>(emptyList())
    val carriageList: StateFlow<List<CarriageDomain>> = _carriageList.asStateFlow()

    fun loadHistory() {
        loadHistoryData()
    }

    fun setNewTrip() {
        viewModelScope.launch {
            try {
                _selectedTripRecord.value?.let { trip ->
                    val isReportSent = completeShiftUseCase(trip.uuid)
                    Log.d("TripViewModel", "Shift completed. Report sent: $isReportSent")
                }
                _selectedTripRecord.update { null }
                _selectedCarriage.update { null }
                _uiState.update { ScreenState.NewTrip }
            } catch (e: Exception) {
                Log.e("TripViewModel", "Error completing shift", e)
                _selectedTripRecord.update { null }
                _selectedCarriage.update { null }
                _uiState.update { ScreenState.NewTrip }
            }
        }
    }

    fun setFindByNumber() { _uiState.value = ScreenState.FindByNumber }
    fun setFindByStation() { _uiState.value = ScreenState.FindByStation }

    fun setSelectCarriage(tripRecord: TripDomain) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _selectedTripRecord.value = tripRecord
                _carriageList.value = getCarriagesForTrainUseCase(tripRecord.uuid) ?: emptyList()
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
            _uiState.update { ScreenState.Error }
        }
    }

    fun checkActiveShift() {
        viewModelScope.launch {
            Log.d("TripViewModel", "Starting checkActiveShift")
            try {
                val shift = getActiveShiftUseCase().first()
                _activeShift.value = shift
                if (shift == null) {
                    _uiState.update { ScreenState.NewTrip }
                    _selectedTripRecord.update { null }
                    _selectedCarriage.update { null }
                    _shiftStatus.update { false }
                    Log.d("TripViewModel", "No active shift")
                } else {
                    _uiState.update { ScreenState.CurrentTrip }
                    _selectedTripRecord.update { shift.trip }
                    _selectedCarriage.update { shift.activeCarriage }
                    _shiftStatus.update { true }
                    Log.d("TripViewModel", "Exists active shift")
                }
            } catch (e: Exception) {
                Log.e("TripViewModel", "Failed to check active shift", e)
                _uiState.update { ScreenState.Error }
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
                _uiState.update { ScreenState.Error }
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

    fun calculatePageSize(
        context: Context,
        @Dimension(unit = Dimension.DP) itemHeightDp: Int = 100
    ): Int {
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        val screenHeightPx = displayMetrics.heightPixels
        val itemHeightPx = (itemHeightDp * displayMetrics.density).toInt()
        val visibleItemsCount = (screenHeightPx / itemHeightPx) * 2
        return visibleItemsCount * 3
    }

    sealed class ScreenState {
        object NewTrip : ScreenState()
        object FindByNumber : ScreenState()
        object FindByStation : ScreenState()
        object SelectCarriage : ScreenState()
        object CurrentTrip: ScreenState()
        object Error: ScreenState()
    }
}
