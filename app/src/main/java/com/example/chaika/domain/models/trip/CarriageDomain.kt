package com.example.chaika.domain.models.trip

/**
 * Доменная модель вагона.
 *
 * @param carNumber Номер вагона.
 * @param classType Тип или класс вагона.
 */
data class CarriageDomain(
    val carNumber: String,
    val classType: String
)