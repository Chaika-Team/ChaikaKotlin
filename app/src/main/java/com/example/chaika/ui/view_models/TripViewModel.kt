package com.example.chaika.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chaika.data.room.entities.Trip
import com.example.chaika.domain.usecases.DeleteTripAndActionsUseCase
import com.example.chaika.domain.usecases.FilterTripsUseCase
import com.example.chaika.domain.usecases.GetAllTripsUseCase
import com.example.chaika.domain.usecases.InsertTripUseCase
import com.example.chaika.domain.usecases.UpdateTripUseCase
import com.example.chaika.usecases.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripViewModel @Inject constructor(
    private val getAllTripsUseCase: GetAllTripsUseCase,
    private val insertTripUseCase: InsertTripUseCase,
    private val deleteTripAndActionsUseCase: DeleteTripAndActionsUseCase,
    private val updateTripUseCase: UpdateTripUseCase,
    private val filterTripsUseCase: FilterTripsUseCase
) : ViewModel() {

    private val _allTrips = MutableStateFlow<List<Trip>>(emptyList())
    private val _filteredTrips = MutableStateFlow<List<Trip>>(emptyList())
    val filteredTrips: StateFlow<List<Trip>> = _filteredTrips.asStateFlow()

    init {
        viewModelScope.launch {
            getAllTripsUseCase.execute().collect { trips ->
                _allTrips.value = trips
                _filteredTrips.value = trips
            }
        }
    }

    fun insertTrip(trip: Trip) = viewModelScope.launch {
        insertTripUseCase.execute(trip)
    }

    fun deleteTripAndActions(trip: Trip) = viewModelScope.launch {
        deleteTripAndActionsUseCase.execute(trip)
    }

    fun updateTrip(trip: Trip) = viewModelScope.launch {
        updateTripUseCase.execute(trip)
    }

    fun filterTrips(query: String) {
        if (query.isEmpty()) {
            // Если запрос пустой, возвращаем весь список
            _filteredTrips.value = _allTrips.value
        } else {
            // Если есть запрос, фильтруем список
            _filteredTrips.value = filterTripsUseCase.execute(_allTrips.value, query)
        }
    }
}
