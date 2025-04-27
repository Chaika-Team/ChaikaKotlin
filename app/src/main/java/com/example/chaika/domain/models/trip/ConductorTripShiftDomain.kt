package com.example.chaika.domain.models.trip

/**
 * Смена проводника:
 * @param trip Данные о поездке.
 * @param activeCarriage Выбранный вагон (или null, если ещё не выбран).
 * @param status Текущий статус смены.
 */
data class ConductorTripShiftDomain(
    val trip: TripDomain,
    val activeCarriage: CarriageDomain?,
    val status: TripShiftStatusDomain
)
