package com.example.chaika.data.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trip_reports")
data class TripReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "route_id") val routeID: String,
    @ColumnInfo(name = "start_time") val startTime: String,
    @ColumnInfo(name = "end_time") val endTime: String,
    @ColumnInfo(name = "carriage_id") val carriageID: String,
    // Здесь храним сериализованный в JSON список операций (например, CartOperationReport)
    @ColumnInfo(name = "carts_json") val cartsJson: String,
    // Флаг, показывающий, что отчёт успешно отправлен на сервер
    @ColumnInfo(name = "is_sent") val isSent: Boolean = false
)
