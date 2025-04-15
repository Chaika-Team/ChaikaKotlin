package com.example.chaika.ui.viewModels


import android.content.Context
import android.util.DisplayMetrics
import androidx.annotation.Dimension
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.chaika.ui.dto.TripRecord
import com.example.chaika.ui.viewModels.TripViewModel.ScreenState.NewTrip
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class TripViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _productsState = mutableStateMapOf<Int, TripRecord>()

    private val _uiState = MutableStateFlow<ScreenState>(NewTrip) // TODO: Запоминать состояние
    val uiState: StateFlow<ScreenState> = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _pagingDataFlow = MutableStateFlow<PagingData<TripRecord>>(PagingData.empty())
    val pagingDataFlow: StateFlow<PagingData<TripRecord>> = _pagingDataFlow.asStateFlow()

    fun setNewTrip() {
        _uiState.value = NewTrip
    }

    fun setFindByNumber() {
        _uiState.value = ScreenState.FindByNumber
    }

    fun setFindByStation() {
        _uiState.value = ScreenState.FindByStation
    }

    fun setSelectCarriage() {
        _uiState.value = ScreenState.SelectCarriage
    }

    fun setCurrentTrip() {
        _uiState.value = ScreenState.CurrentTrip
    }

    fun loadHistory() {
        loadHistoryData()
        getHistory()
    }

    private fun loadHistoryData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                fetchAndSaveHistoryUseCase()
            } catch (e: Exception) {
                _uiState.update { ScreenState.Error }
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun getHistory() {
        viewModelScope.launch {
            getPagedHistoryUseCase(
                pageSize = calculatePageSize(context = context)
            ).map { pagingData ->
                pagingData.map { tripRecord ->
                    _productsState.getOrPut(tripRecord.routeID) {
                        TripRecord(
                            routeID = tripRecord.routeID,
                            trainId = tripRecord.trainId,
                            startTime = tripRecord.startTime,
                            endTime = tripRecord.endTime,
                            carriageID = tripRecord.carriageID
                        )
                    }
                }
            }
                .cachedIn(viewModelScope)
                .collect { pagingData ->
                    _pagingDataFlow.value = pagingData
                }
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

// Mock implementation of fetchAndSaveHistoryUseCase
private fun fetchAndSaveHistoryUseCase(): List<TripRecord> {
    // Mock data - in real app this would fetch from API and save to DB
    return listOf(
        TripRecord(
            routeID = 0,
            trainId = "119A",
            startTime = LocalDateTime.parse("2024-03-30T00:12:00"),
            endTime = LocalDateTime.parse("2024-03-30T09:47:00"),
            carriageID = 33
        )
    )
}

// Mock implementation of getPagedHistoryUseCase
private fun getPagedHistoryUseCase(pageSize: Int): Flow<PagingData<TripRecord>> {
    // Generate mock trip records
    val mockTrips = (1..100).map { id ->
        TripRecord(
            routeID = id,
            trainId = "TR${(100 + id)}",
            startTime = LocalDateTime.now().minusDays(id.toLong()),
            endTime = LocalDateTime.now().minusDays(id.toLong()).plusHours(5),
            carriageID = id % 10 + 1
        )
    }

    return flow {
        emit(PagingData.from(mockTrips))
    }
}