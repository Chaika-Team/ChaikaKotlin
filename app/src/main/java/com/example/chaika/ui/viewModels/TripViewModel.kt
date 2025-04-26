package com.example.chaika.ui.viewModels


import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import androidx.annotation.Dimension
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chaika.domain.models.trip.CarriageDomain
import com.example.chaika.domain.models.trip.StationDomain
import com.example.chaika.domain.models.trip.TripDomain
import com.example.chaika.domain.usecases.GetCarriagesForTrainUseCase
import com.example.chaika.domain.usecases.SearchTripsByStationsUseCase
import com.example.chaika.domain.usecases.SuggestStationsUseCase
import com.example.chaika.ui.viewModels.tripMocks.fetchAndSaveHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TripViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getCarriagesForTrainUseCase: GetCarriagesForTrainUseCase,
    private val searchTripByStationUseCase: SearchTripsByStationsUseCase,
    private val suggestStationsUseCase: SuggestStationsUseCase
) : ViewModel() {
    private val _selectedTripRecord = MutableStateFlow<TripDomain?>(null)

    private val _selectedCarriage = MutableStateFlow<CarriageDomain?>(null)

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
            _selectedTripRecord.update { null }
            _selectedCarriage.update { null }
            _uiState.update { ScreenState.NewTrip }
        }
    }

    fun setFindByNumber() {
        _uiState.value = ScreenState.FindByNumber
    }

    fun setFindByStation() {
        _uiState.value = ScreenState.FindByStation
    }

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
            _uiState.value = ScreenState.CurrentTrip
        }
    }

    fun getSelectedTrip(): TripDomain? {
        return _selectedTripRecord.value
    }

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
        viewModelScope.launch {
            _pagingHistoryFlow.value = fetchAndSaveHistoryUseCase()

        }
    }

    fun getTrips(searchDate: String, searchStart: Int, searchFinish: Int) {
        viewModelScope.launch {
            _foundTripsList.value = searchTripByStationUseCase(
                searchDate, searchStart, searchFinish
            )
        }
    }

    suspend fun suggestStations(query: String, limit: Int): List<StationDomain> {
        return withContext(Dispatchers.IO) {
            suggestStationsUseCase(query, limit)
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