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

    @Query("SELECT * FROM conductors WHERE employee_id = :employeeID")
    suspend fun getConductorByEmployeeID(employeeID: String): Conductor?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConductor(conductor: Conductor)

    @Update
    suspend fun updateConductor(conductor: Conductor)

    @Delete
    suspend fun deleteConductor(conductor: Conductor)

    // Новый метод для удаления всех проводников
    @Query("DELETE FROM conductors")
    suspend fun deleteAllConductors()
}
