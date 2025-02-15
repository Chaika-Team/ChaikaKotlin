package com.example.chaika.data.room.mappers

import com.example.chaika.data.room.entities.Conductor
import com.example.chaika.domain.models.ConductorDomain

/**
 * Преобразует сущность проводника в доменную модель.
 *
 * @return Доменная модель `ConductorDomain`.
 */
fun Conductor.toDomain(): ConductorDomain {
    return ConductorDomain(
        id = this.id,
        name = this.name,
        familyName = this.familyName,
        givenName = this.givenName,
        employeeID = this.employeeID,
        image = this.image
    )
}

/**
 * Преобразует доменную модель проводника в сущность для базы данных.
 *
 * @return Сущность `Conductor`.
 */
fun ConductorDomain.toEntity(): Conductor {
    return Conductor(
        id = this.id ?: 0, // Если id null, используем 0 (Room сгенерирует новый)
        name = this.name,
        familyName = this.familyName,
        givenName = this.givenName,
        employeeID = this.employeeID,
        image = this.image
    )
}

