package com.example.chaika.activities.mainActivity

import androidx.lifecycle.LiveData
import com.example.chaika.dataBase.dao.TripDao
import com.example.chaika.dataBase.entities.Trip


class TripRepository(private val tripDao: TripDao) {
    suspend fun insertTrip(trip: Trip) {
        tripDao.insertTrip(trip)
    }

    suspend fun deleteTrip(trip: Trip) {
        tripDao.deleteTrip(trip)
    }

    suspend fun updateTrip(trip: Trip){
        tripDao.updateTrip(trip)
    }

    fun getAllTrips(): LiveData<List<Trip>> = tripDao.getAllTrips()
}
