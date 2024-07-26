package com.example.chaika.dataBase.models

import androidx.lifecycle.LiveData
import com.example.chaika.dataBase.dao.TripDao
import com.example.chaika.dataBase.entities.Trip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class TripRepository(private val tripDao: TripDao, private val actionRepository: ActionRepository) {
    suspend fun insertTrip(trip: Trip) {
        tripDao.insertTrip(trip)
    }

    suspend fun deleteTrip(trip: Trip) {
        tripDao.deleteTrip(trip)
    }

    suspend fun updateTrip(trip: Trip){
        tripDao.updateTrip(trip)
    }

    suspend fun deleteTripAndActions(trip: Trip) {
        withContext(Dispatchers.IO) {
            actionRepository.deleteActionsByTripId(trip.id)
            tripDao.deleteTrip(trip)
        }
    }

    fun getAllTrips(): LiveData<List<Trip>> = tripDao.getAllTrips()
}
