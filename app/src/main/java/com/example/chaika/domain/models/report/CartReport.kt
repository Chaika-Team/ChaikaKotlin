package com.example.chaika.domain.models.report

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Пэйлоад одной операции с корзиной в смене.
 *
 * @param cartId         Данные об операции (сотрудник + время).
 * @param operationType  Тип операции (Int, по договорённости: 0–ADD, 1–SOLD_CASH и т.д.).
 * @param items          Список позиций (товаров) в этой операции.
 */
@JsonClass(generateAdapter = true)
data class CartReport(
    @Json(name = "cart_id")        val cartId: CartIdReport,
    @Json(name = "operation_type") val operationType: Int,
    @Json(name = "items")          val items: List<CartItemReport>
)