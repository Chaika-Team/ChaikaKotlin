package com.example.chaika.dataBase.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.chaika.dataBase.entities.Action

@Dao
interface ActionDao {

    @Insert
    suspend fun insert(action: Action)

    @Query("SELECT * FROM actions WHERE trip_id = :tripId")
    fun getActionsByTripId(tripId: Int): LiveData<List<Action>>

    @Query("DELETE FROM actions WHERE trip_id = :tripId")
    suspend fun deleteActionsByTripId(tripId: Int)

    @Query("DELETE FROM actions WHERE product_id = :productId AND trip_id = :tripId")
    suspend fun deleteActionsForProductInTrip(productId: Int, tripId: Int)

// TODO: Здесь будут необходимые методы

}