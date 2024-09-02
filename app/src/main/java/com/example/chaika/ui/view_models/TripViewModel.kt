package com.example.chaika.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chaika.domain.models.old.Trip
import com.example.chaika.domain.usecases.old.DeleteTripAndActionsUseCase
import com.example.chaika.domain.usecases.old.FilterTripsUseCase
import com.example.chaika.domain.usecases.old.GetAllTripsUseCase
import com.example.chaika.domain.usecases.old.InsertTripUseCase
import com.example.chaika.domain.usecases.old.UpdateTripUseCase
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

    private val _allTrips = MutableStateFlow<List<Trip>>(emptyList()) // Доменная модель
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
            _filteredTrips.value = _allTrips.value
        } else {
            _filteredTrips.value = filterTripsUseCase.execute(_allTrips.value, query)
        }
    }
}
