package com.example.chaika.domain.models.report

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Пэйлоад верхнего уровня для JSON-отчёта по смене проводника.
 *
 * @param tripId      Данные о маршруте и времени старта поездки.
 * @param endTime     Время окончания поездки в формате ISO 8601.
 * @param carriageId  Идентификатор (номер) вагона.
 * @param carts       Список операций с корзинами за смену.
 */
@JsonClass(generateAdapter = true)
data class ShiftReportReport(
    @Json(name = "trip_id")     val tripId: TripIdReport,
    @Json(name = "end_time")    val endTime: String,
    @Json(name = "carriage_id") val carriageId: Int,
    @Json(name = "carts")       val carts: List<CartReport>
)