package com.example.chaika.data.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conductors")
data class Conductor(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "employee_id") val employeeID: String, // Новый столбец
    @ColumnInfo(name = "image") val image: String
)
