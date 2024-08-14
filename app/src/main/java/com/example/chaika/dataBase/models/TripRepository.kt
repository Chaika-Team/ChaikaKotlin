package com.example.chaika.dataBase.models

import com.example.chaika.dataBase.dao.TripDao
import com.example.chaika.dataBase.entities.Trip
import kotlinx.coroutines.flow.Flow

class TripRepository(private val tripDao: TripDao) {

    fun getAllTrips(): Flow<List<Trip>> {
        return tripDao.getAllTrips()
    }

    suspend fun insertTrip(trip: Trip) {
        tripDao.insertTrip(trip)
    }

    suspend fun deleteTrip(trip: Trip) {
        tripDao.deleteTrip(trip)
    }

//    suspend fun deleteTripAndActions(trip: Trip) {
//        tripDao.deleteTripAndActions(trip)
//    }

    suspend fun updateTrip(trip: Trip) {
        tripDao.updateTrip(trip)
    }
}
