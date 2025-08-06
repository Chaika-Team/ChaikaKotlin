package com.example.chaika.domain.models.report

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Данные, однозначно идентифицирующие одну операцию корзины.
 *
 * @param employeeId     Табельный номер сотрудника.
 * @param operationTime  Время выполнения операции в ISO 8601.
 */
@JsonClass(generateAdapter = true)
data class CartIdReport(
    @Json(name = "employee_id")    val employeeId: String,
    @Json(name = "operation_time") val operationTime: String
)