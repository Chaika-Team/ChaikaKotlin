package com.example.chaika.services

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chaika.dataBase.entities.Trip
import com.example.chaika.dataBase.models.TripRepository
import kotlinx.coroutines.launch

class TripViewModel(private val repository: TripRepository) : ViewModel() {

    val allTrips: LiveData<List<Trip>> = repository.getAllTrips()

    private val _filteredTrips = MutableLiveData<List<Trip>>(emptyList())
    val filteredTrips: LiveData<List<Trip>> = _filteredTrips

    init {
        // Подписка на изменения в allTrips
        allTrips.observeForever { trips ->
            _filteredTrips.value = trips ?: emptyList()
        }
    }

    fun insertTrip(trip: Trip) = viewModelScope.launch {
        repository.insertTrip(trip)
    }

    fun deleteTrip(trip: Trip) = viewModelScope.launch {
        repository.deleteTrip(trip)
    }

    fun deleteTripAndActions(trip: Trip) = viewModelScope.launch {
        repository.deleteTripAndActions(trip)
    }

    fun updateTrip(trip: Trip) = viewModelScope.launch {
        repository.updateTrip(trip)
    }

    fun filterTrips(query: String) {
        val allTripsList = allTrips.value ?: emptyList()
        _filteredTrips.value = if (query.isEmpty()) {
            allTripsList
        } else {
            allTripsList.filter { trip ->
                trip.name.lowercase().contains(query.lowercase())
            }
        }
    }
}
