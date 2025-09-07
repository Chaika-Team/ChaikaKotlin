package com.chaikasoft.app.data.dataSource.mappers

import com.chaikasoft.app.data.dataSource.dto.*
import com.chaikasoft.app.domain.models.trip.CarriageDomain
import com.chaikasoft.app.domain.models.trip.StationDomain
import com.chaikasoft.app.domain.models.trip.TripDomain

fun StationDto.toDomain(): StationDomain = StationDomain(
    code = code,
    name = name,
    city = city
)

fun TripDto.toDomain(): TripDomain = TripDomain(
    uuid = uuid,
    trainNumber = trainNumber,
    departure = departure,
    arrival = arrival,
    duration = duration,
    from = from.toDomain(),
    to = to.toDomain()
)

fun CarDto.toDomain(): CarriageDomain = CarriageDomain(
    carNumber = carNumber,
    classType = classType
)