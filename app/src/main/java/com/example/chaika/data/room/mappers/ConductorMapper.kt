package com.example.chaika.data.room.mappers

import com.example.chaika.data.room.entities.Conductor as ConductorEntity
import com.example.chaika.domain.models.Conductor as ConductorDomain

fun ConductorEntity.toDomain(): ConductorDomain {
    return ConductorDomain(
        id = this.id,
        name = this.name,
        image = this.image
    )
}

fun ConductorDomain.toEntity(): ConductorEntity {
    return ConductorEntity(
        id = this.id,
        name = this.name,
        image = this.image
    )
}
