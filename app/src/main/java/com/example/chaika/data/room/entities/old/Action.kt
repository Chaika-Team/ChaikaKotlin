package com.example.chaika.data.room.entities.old

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    tableName = "actions",
    foreignKeys = [
        ForeignKey(entity = Product::class, parentColumns = ["id"], childColumns = ["product_id"]),
        ForeignKey(entity = Operation::class, parentColumns = ["id"], childColumns = ["operation_id"]),
        ForeignKey(entity = Trip::class, parentColumns = ["id"], childColumns = ["trip_id"])
    ]
)

data class Action(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "time") val time: String,
    @ColumnInfo(name = "trip_id") val tripId: Int,
    @ColumnInfo(name = "operation_id") val operationId: Int,
    @ColumnInfo(name = "product_id") val productId: Int,
    @ColumnInfo(name = "count") val count: Int
)