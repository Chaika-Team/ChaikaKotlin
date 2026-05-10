package com.chaikasoft.app.data.datasource.mappers

import com.chaikasoft.app.data.datasource.dto.ConductorDto
import com.chaikasoft.app.domain.models.ConductorDomain

/**
 * Преобразует ConductorDto (данные с сервера) в доменную модель ConductorDomain.
 * Здесь поле nickname маппится на employeeID, а id остаётся неустановленным (например, 0 или null),
 * поскольку сервер не передаёт его.
 */
fun ConductorDto.toDomain(): ConductorDomain = ConductorDomain(
    id = 0, // или можно сделать id: Int? и оставить null
    name = this.name,
    familyName = this.familyName,
    givenName = this.givenName,
    employeeID = this.nickname,
    image = this.image
        ?: "https://i.pinimg.com/736x/5b/d3/e7/5bd3e779f192cb04cf35b859e0d50cbc.jpg"
)
