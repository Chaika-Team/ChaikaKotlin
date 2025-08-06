package com.example.chaika.domain.models.report

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Описание одной позиции (товара) в операции.
 *
 * @param productId  Идентификатор товара.
 * @param quantity   Количество единиц.
 * @param price      Цена за единицу (целое число, без копеек).
 */
@JsonClass(generateAdapter = true)
data class CartItemReport(
    @Json(name = "product_id") val productId: Int,
    @Json(name = "quantity") val quantity: Int,
    @Json(name = "price") val price: Double
)