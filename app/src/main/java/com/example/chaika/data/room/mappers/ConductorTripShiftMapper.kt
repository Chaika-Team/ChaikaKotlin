package com.example.chaika.data.room.mappers

import com.example.chaika.data.room.entities.ConductorTripShift
import com.example.chaika.domain.models.trip.CarriageDomain
import com.example.chaika.domain.models.trip.ConductorTripShiftDomain
import com.example.chaika.domain.models.trip.TripDomain
import com.example.chaika.domain.models.trip.TripShiftStatusDomain
import com.example.chaika.domain.models.trip.StationDomain

/** Преобразование Int → TripShiftStatusDomain */
fun Int.toTripShiftStatusDomain(): TripShiftStatusDomain =
    TripShiftStatusDomain.entries.getOrNull(this)
        ?: throw IllegalArgumentException("Unknown TripShiftStatusDomain ordinal=$this")

/** Преобразование TripShiftStatusDomain → Int */
fun TripShiftStatusDomain.toInt(): Int = this.ordinal

/** Entity → Domain */
fun ConductorTripShift.toDomain(): ConductorTripShiftDomain =
    ConductorTripShiftDomain(
        trip = TripDomain(
            uuid = uuid,
            trainNumber = trainNumber,
            departure = departure,
            arrival = arrival,
            duration = duration,
            from = StationDomain(from.code, from.name, from.city),
            to = StationDomain(to.code, to.name, to.city)
        ),
        activeCarriage = activeCarriage?.let {
            CarriageDomain(it.carNumber, it.classType)
        },
        status = status.toTripShiftStatusDomain()
    )

/** Domain → Entity */
fun ConductorTripShiftDomain.toEntity(): ConductorTripShift {
    val now = System.currentTimeMillis()
    return ConductorTripShift(
        uuid = this.trip.uuid,
        trainNumber = this.trip.trainNumber,
        departure = this.trip.departure,
        arrival = this.trip.arrival,
        duration = this.trip.duration,
        from = StationDomain(
            code = this.trip.from.code,
            name = this.trip.from.name,
            city = this.trip.from.city
        ),
        to = StationDomain(
            code = this.trip.to.code,
            name = this.trip.to.name,
            city = this.trip.to.city
        ),
        activeCarriage = this.activeCarriage,
        status = this.status.toInt(),
        report = null,
        createdAt = now,
        updatedAt = null
    )
}
