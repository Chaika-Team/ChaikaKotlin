package com.example.chaika.dataBase.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.chaika.dataBase.entities.Trip

@Dao
interface TripDao {

    @Query("SELECT * FROM trips ORDER BY id DESC")
    fun getAllTrips(): LiveData<List<Trip>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: Trip): Long // Возвращает ID вставленной поездки

    @Update
    suspend fun updateTrip(trip: Trip)

    @Delete
    suspend fun deleteTrip(trip: Trip)

    @Query("SELECT * FROM trips WHERE id = :tripId")
    fun getTrip(tripId: Int): LiveData<Trip>

    // Дополнительные методы по необходимости...
}
