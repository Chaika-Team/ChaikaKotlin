package com.example.chaika.data.room.dao

import androidx.room.*
import com.example.chaika.data.room.entities.Trip
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {

    @Query("SELECT * FROM trips ORDER BY id DESC")
    fun getAllTrips(): Flow<List<Trip>> // Используем Flow вместо LiveData

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: Trip): Long // Возвращает ID вставленной поездки

    @Update
    suspend fun updateTrip(trip: Trip)

    @Delete
    suspend fun deleteTrip(trip: Trip)

    @Query("SELECT * FROM trips WHERE id = :tripId")
    fun getTrip(tripId: Int): Flow<Trip> // Здесь тоже можно использовать Flow

    // Дополнительные методы по необходимости...
}
