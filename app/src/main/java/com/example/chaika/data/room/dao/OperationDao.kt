package com.example.chaika.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.chaika.data.room.entities.Operation

@Dao
interface OperationDao {
    @Query("SELECT * FROM operations")
    fun getAllOperations(): LiveData<List<Operation>>

    @Insert
    suspend fun insertOperation(operation: Operation)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(operations: List<Operation>)

    // TODO: Здесь будут необходимые методы

}