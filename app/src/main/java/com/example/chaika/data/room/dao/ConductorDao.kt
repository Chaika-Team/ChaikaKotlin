package com.example.chaika.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.chaika.data.room.entities.Conductor
import kotlinx.coroutines.flow.Flow

@Dao
interface ConductorDao {

    @Query("SELECT * FROM conductors")
    fun getAllConductors(): Flow<List<Conductor>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConductor(conductor: Conductor)

    @Update
    suspend fun updateConductor(conductor: Conductor)

    @Delete
    suspend fun deleteConductor(conductor: Conductor)
}
