package com.example.chaika.data.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "cart_operations",
    foreignKeys = [
        ForeignKey(
            entity = Conductor::class,
            parentColumns = ["id"],
            childColumns = ["conductor_id"]
        )
    ]
)
data class CartOperation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "operation_type") val operationType: Int, // mapper
    @ColumnInfo(name = "operation_time") val operationTime: String, // Установится в маппере
    @ColumnInfo(name = "conductor_id") val conductorId: Int
)
