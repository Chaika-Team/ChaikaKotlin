package com.example.chaika.data.room.repo.old

import com.example.chaika.data.room.mappers.old.toDomain
import com.example.chaika.data.room.mappers.old.toEntity
import com.example.chaika.data.room.dao.old.TripDao
import com.example.chaika.domain.models.old.Trip
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TripRepository(private val tripDao: TripDao) {

    fun getAllTrips(): Flow<List<Trip>> {
        return tripDao.getAllTrips().map { trips ->
            trips.map { it.toDomain() }
        }
    }

    suspend fun insertTrip(trip: Trip) {
        tripDao.insertTrip(trip.toEntity())
    }

    suspend fun deleteTrip(trip: Trip) {
        tripDao.deleteTrip(trip.toEntity())
    }

    suspend fun updateTrip(trip: Trip) {
        tripDao.updateTrip(trip.toEntity())
    }
}
