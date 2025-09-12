// data/room/mappers/StationMapper.kt
package com.chaikasoft.app.data.room.mappers
import com.chaikasoft.app.data.room.entities.Station
import com.chaikasoft.app.domain.models.trip.StationDomain

fun Station.toDomain() = StationDomain(code, name, city)
fun StationDomain.toEntity() = Station(code, name, city)
