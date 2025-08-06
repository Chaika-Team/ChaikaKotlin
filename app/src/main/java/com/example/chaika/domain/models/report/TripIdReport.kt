package com.example.chaika.domain.models.report

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Информация об идентификаторе поездки.
 *
 * @param routeId    Строковый код маршрута.
 * @param startTime  Время отправления в формате ISO 8601.
 */
@JsonClass(generateAdapter = true)
data class TripIdReport(
    @Json(name = "route_id")   val routeId: String,
    @Json(name = "start_time") val startTime: String
)