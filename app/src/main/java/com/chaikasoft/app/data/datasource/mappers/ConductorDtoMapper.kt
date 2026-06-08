package com.chaikasoft.app.data.datasource.mappers

import com.chaikasoft.app.data.datasource.dto.ConductorDto
import com.chaikasoft.app.domain.models.ConductorDomain

/**
 * Преобразует ConductorDto (данные с сервера) в доменную модель ConductorDomain.
 * Поля берутся из смысловых OIDC claims: given_name, family_name, middle_name,
 * preferred_username и picture.
 */
fun ConductorDto.toDomain(): ConductorDomain = ConductorDomain(
    id = 0, // или можно сделать id: Int? и оставить null
    name = this.firstName,
    familyName = this.familyName,
    givenName = this.middleName?.trim().orEmpty(),
    employeeID = this.preferredUsername,
    image = this.picture?.trim().orEmpty()
)
