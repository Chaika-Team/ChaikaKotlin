package com.example.chaika.domain.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TripReport(
    @Json(name = "RouteID") val routeID: String,
    @Json(name = "StartTime") val startTime: String,
    @Json(name = "EndTime") val endTime: String,
    @Json(name = "CarriageID") val carriageID: String,
    @Json(name = "Carts") val carts: List<CartOperationReport>
)

@JsonClass(generateAdapter = true)
data class CartOperationReport(
    @Json(name = "EmployeeID") val employeeID: String,
    @Json(name = "OperationType") val operationType: Int,
    @Json(name = "OperationTime") val operationTime: String,
    @Json(name = "Items") val items: List<CartItemReport>
)

@JsonClass(generateAdapter = true)
data class CartItemReport(
    @Json(name = "ProductID") val productID: Int,
    @Json(name = "Quantity") val quantity: Int,
    @Json(name = "Price") val price: Double
)
