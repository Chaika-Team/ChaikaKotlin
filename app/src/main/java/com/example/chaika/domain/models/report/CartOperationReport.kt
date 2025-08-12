package com.example.chaika.domain.models.report

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Репорт-модель операции с корзиной.
 *
 * @param employeeID Табельный номер проводника, выполняющего операцию.
 * @param operationType Тип операции (0 - ADD, 1 - SOLD_CASH, 2 - SOLD_CART, 3 - REPLENISH).
 * @param operationTime Время выполнения операции.
 * @param items Список товаров, связанных с операцией.
 */
@JsonClass(generateAdapter = true)
data class CartOperationReport(
    @Json(name = "EmployeeID") val employeeID: String,
    @Json(name = "OperationType") val operationType: Int,
    @Json(name = "OperationTime") val operationTime: String,
    @Json(name = "Items") val items: List<CartItemReport>,
)