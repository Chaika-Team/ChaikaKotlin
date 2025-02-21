package com.example.chaika.domain.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Репорт-модель поездки.
 *
 * @param routeID Уникальный идентификатор маршрута.
 * @param startTime Время начала поездки.
 * @param endTime Время окончания поездки.
 * @param carriageID Номер вагона.
 * @param carts Список операций с корзинами.
 */
@JsonClass(generateAdapter = true)
data class TripReport(
    @Json(name = "RouteID") val routeID: String,
    @Json(name = "StartTime") val startTime: String,
    @Json(name = "EndTime") val endTime: String,
    @Json(name = "CarriageID") val carriageID: String,
    @Json(name = "Carts") val carts: List<CartOperationReport>,
)

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

/**
 * Репорт-модель элемента корзины.
 *
 * @param productID Уникальный идентификатор продукта.
 * @param quantity Количество единиц продукта.
 * @param price Цена продукта.
 */
@JsonClass(generateAdapter = true)
data class CartItemReport(
    @Json(name = "ProductID") val productID: Int,
    @Json(name = "Quantity") val quantity: Int,
    @Json(name = "Price") val price: Double,
)
