package com.example.chaika.domain.models.trip

/**
 * Доменная модель поездки.
 *
 * @param uuid Уникальный идентификатор поездки.
 * @param trainNumber Номер или идентификатор поезда.
 * @param departure Время отправления (формат RFC3339, например "2006-01-02T15:04:05Z07:00").
 * @param arrival Время прибытия (формат RFC3339).
 * @param duration Продолжительность поездки (формат ISO 8601, например "PT2D3H4M" — 2 дня 3 часа 4 минуты).
 * @param from Станция отправления.
 * @param to Станция прибытия.
 */
data class TripDomain(
    val uuid: String,
    val trainNumber: String,
    val departure: String,
    val arrival: String,
    val duration: String,
    val from: StationDomain,
    val to: StationDomain
)