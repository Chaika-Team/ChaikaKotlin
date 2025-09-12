package com.chaikasoft.app.data.room.mappers

import com.chaikasoft.app.data.room.entities.ConductorTripShift
import com.chaikasoft.app.data.room.relations.ConductorTripShiftWithStations
import com.chaikasoft.app.data.room.mappers.toDomain as stationToDomain
import com.chaikasoft.app.domain.models.trip.*

/** Преобразование Int → TripShiftStatusDomain */
fun Int.toTripShiftStatusDomain(): TripShiftStatusDomain =
    TripShiftStatusDomain.entries.getOrNull(this)
        ?: throw IllegalArgumentException("Unknown TripShiftStatusDomain ordinal=$this")

/** Преобразование TripShiftStatusDomain → Int */
fun TripShiftStatusDomain.toInt(): Int = this.ordinal

/** Entity → Domain */
fun ConductorTripShiftWithStations.toDomain(): ConductorTripShiftDomain =
    ConductorTripShiftDomain(
        trip = TripDomain(
            uuid       = shift.uuid,
            trainNumber= shift.trainNumber,
            departure  = shift.departure,
            arrival    = shift.arrival,
            duration   = shift.duration,
            from       = from.stationToDomain(),
            to         = to.stationToDomain()
        ),
        activeCarriage = shift.activeCarriage,
        status         = shift.status.toTripShiftStatusDomain()
    )

/** Domain → Entity */
fun ConductorTripShiftDomain.toEntity(): ConductorTripShift {
    val now = System.currentTimeMillis()
    return ConductorTripShift(
        uuid         = trip.uuid,
        trainNumber  = trip.trainNumber,
        departure    = trip.departure,
        arrival      = trip.arrival,
        duration     = trip.duration,
        fromCode     = trip.from.code,  // ← только коды
        toCode       = trip.to.code,
        activeCarriage = activeCarriage,
        status       = status.toInt(),
        report       = null,
        createdAt    = now,
        updatedAt    = null
    )
}
