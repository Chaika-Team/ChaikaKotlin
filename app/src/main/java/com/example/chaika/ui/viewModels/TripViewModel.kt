package com.example.chaika.ui.viewModels


import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import androidx.annotation.Dimension
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.chaika.ui.dto.Carriage
import com.example.chaika.ui.dto.TripRecord
import com.example.chaika.ui.viewModels.tripMocks.fetchAndSaveHistoryUseCase
import com.example.chaika.ui.viewModels.tripMocks.getPagedCarriagesUseCase
import com.example.chaika.ui.viewModels.tripMocks.getPagedFutureTripsUseCase
import com.example.chaika.ui.viewModels.tripMocks.getPagedHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _selectedTripRecord = MutableStateFlow<TripRecord?>(null)

    private val _uiState = MutableStateFlow<ScreenState>(ScreenState.NewTrip)
    val uiState: StateFlow<ScreenState> = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _pagingHistoryFlow = MutableStateFlow<PagingData<TripRecord>>(PagingData.empty())
    val pagingHistoryFlow: StateFlow<PagingData<TripRecord>> = _pagingHistoryFlow.asStateFlow()

    private val _pagingFoundTripsFlow = MutableStateFlow<PagingData<TripRecord>>(PagingData.empty())
    val pagingFoundTripsFlow: StateFlow<PagingData<TripRecord>> = _pagingFoundTripsFlow.asStateFlow()

    private val _pagingCarriageFlow = MutableStateFlow<PagingData<Carriage>>(PagingData.empty())
    val pagingCarriageFlow: StateFlow<PagingData<Carriage>> = _pagingCarriageFlow.asStateFlow()

    fun loadHistory() {
        loadHistoryData()
        getHistory()
    }

    fun setNewTrip() {
        _uiState.value = ScreenState.NewTrip
    }

    fun setFindByNumber() {
        _uiState.value = ScreenState.FindByNumber
    }

    fun setFindByStation() {
        _uiState.value = ScreenState.FindByStation
    }

    fun setSelectCarriage(tripRecord: TripRecord) {
        _selectedTripRecord.value = tripRecord
        _uiState.value = ScreenState.SelectCarriage
        loadCarriages(tripRecord.trainId)
    }

    fun setCurrentTrip() {
        _uiState.value = ScreenState.CurrentTrip
    }

    fun getSelectedTrip(): TripRecord? {
        return _selectedTripRecord.value
    }

    fun loadHistoryData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                fetchAndSaveHistoryUseCase()
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
            getPagedHistoryUseCase(
                pageSize = calculatePageSize(context)
            )
                .cachedIn(viewModelScope)
                .collect { pagingData ->
                    _pagingHistoryFlow.value = pagingData
                }
        }
    }

    fun getTrips(searchDate: String, searchStart: String, searchFinish: String) {
        viewModelScope.launch {
            getPagedFutureTripsUseCase(
                pageSize = calculatePageSize(context),
            )
                .cachedIn(viewModelScope)
                .collect { pagingData ->
                    _pagingFoundTripsFlow.value = pagingData
                }
        }
    }


    fun loadCarriages(trainId: String) {
        viewModelScope.launch {
            getPagedCarriagesUseCase(
                pageSize = calculatePageSize(context),
                trainId = trainId
            ).cachedIn(viewModelScope)
                .collect { pagingData ->
                    _pagingCarriageFlow.value = pagingData
                }
        }
    }

    fun createNewTrip() {
        viewModelScope.launch {
            // TODO(Логика создания новой поездки)
            _uiState.update { ScreenState.CurrentTrip }
        }
    }

    fun finishCurrentTrip() {
        // TODO(Логика завершения поездки)
        _uiState.update { ScreenState.NewTrip }
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

