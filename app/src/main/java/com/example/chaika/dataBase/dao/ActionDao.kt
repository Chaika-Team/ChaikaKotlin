package com.example.chaika.dataBase.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.chaika.dataBase.entities.Action

@Dao
interface ActionDao {
    @Query("SELECT * FROM actions")
    fun getAllActions(): LiveData<List<Action>>

    @Insert
    suspend fun insertAction(action: Action)

    // TODO: Здесь будут необходимые методы

}