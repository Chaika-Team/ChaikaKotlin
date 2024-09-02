package com.example.chaika.data.room.mappers.old

import com.example.chaika.data.room.entities.old.Trip as TripEntity
import com.example.chaika.domain.models.old.Trip as TripDomain

fun TripEntity.toDomain(): TripDomain {
    return TripDomain(
        id = this.id,
        name = this.name,
        date = this.date
    )
}

fun TripDomain.toEntity(): TripEntity {
    return TripEntity(
        id = this.id,
        name = this.name,
        date = this.date
    )
}
