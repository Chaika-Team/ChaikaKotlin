package com.chaikasoft.app.data.room.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.chaikasoft.app.domain.models.trip.CarriageDomain

/**
 * Entity для хранения текущей смены проводника.
 */
@Entity(
    tableName = "conductor_trip_shifts",
    foreignKeys = [
        ForeignKey(
            entity = Station::class,
            parentColumns = ["code"],
            childColumns = ["from_code"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = Station::class,
            parentColumns = ["code"],
            childColumns = ["to_code"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("from_code"), Index("to_code")]
)
data class ConductorTripShift(
    @PrimaryKey val uuid: String,
    val trainNumber: String,
    val departure: String,
    val arrival: String,
    val duration: String,
    @ColumnInfo(name = "from_code") val fromCode: String,
    @ColumnInfo(name = "to_code")   val toCode: String,
    @Embedded(prefix = "carriage_") val activeCarriage: CarriageDomain?,
    val status: Int,          // TripShiftStatusDomain.ordinal
    val report: String?,      // JSON отчёта (или null)
    val createdAt: Long,
    val updatedAt: Long?
)
