package com.example.chaika.data.room.mappers

import com.example.chaika.data.room.entities.Conductor
import com.example.chaika.domain.models.ConductorDomain

fun Conductor.toDomain(): ConductorDomain {
    return ConductorDomain(
        id = this.id,
        name = this.name,
        employeeID = this.employeeID,
        image = this.image
    )
}

fun ConductorDomain.toEntity(): Conductor {
    return Conductor(
        id = this.id,
        name = this.name,
        employeeID = this.employeeID,
        image = this.image
    )
}
