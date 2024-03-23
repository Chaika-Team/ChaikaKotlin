package com.example.chaika.dataBase.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.chaika.dataBase.entities.Operation

@Dao
interface OperationDao {
    @Query("SELECT * FROM operations")
    fun getAllOperations(): LiveData<List<Operation>>

    @Insert
    suspend fun insertOperation(operation: Operation)

    // TODO: Здесь будут необходимые методы

}