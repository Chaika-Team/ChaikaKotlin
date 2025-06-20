package com.example.chaika.data.room.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.chaika.domain.models.trip.CarriageDomain
import com.example.chaika.domain.models.trip.StationDomain

/**
 * Entity для хранения текущей смены проводника.
 */
@Entity(tableName = "conductor_trip_shifts")
data class ConductorTripShift(
    @PrimaryKey
    val uuid: String,               // совпадает с uuid в TripDomain

    // TripDomain
    val trainNumber: String,
    val departure: String,
    val arrival: String,
    val duration: String,

    @Embedded(prefix = "from_")
    val from: StationDomain,        // code, name, city

    @Embedded(prefix = "to_")
    val to: StationDomain,          // code, name, city

    // CarriageDomain — может быть null (еще не выбран вагон)
    @Embedded(prefix = "carriage_")
    val activeCarriage: CarriageDomain?,

    // Статус рейса: ACTIVE, FINISHED, SENT
    val status: Int,

    // JSON-отчет, сформированный при завершении (или null)
    val report: String?,

    // Временные метки (millis since epoch)
    val createdAt: Long,
    val updatedAt: Long?
)
