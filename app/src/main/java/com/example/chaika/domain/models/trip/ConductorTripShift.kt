package com.example.chaika.domain.models.trip

/**
 * Модель для назначения смены проводника.
 *
 * @param trip Данные о поездке.
 * @param activeCarriage Активный вагон, в котором находится проводник.
 */
data class ConductorTripShift(
    val trip: TripDomain,
    val activeCarriage: CarriageDomain?
)
