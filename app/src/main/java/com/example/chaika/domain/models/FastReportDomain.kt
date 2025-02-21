package com.example.chaika.domain.models

/**
 * Доменная модель быстрого отчёта.
 * Используется для представления промежуточной информации о продажах.
 *
 * @param productName Название продукта.
 * @param productPrice Цена продукта.
 * @param addedQuantity Количество добавленных единиц продукта.
 * @param replenishedQuantity Количество пополненных единиц продукта.
 * @param soldCashQuantity Количество проданных за наличные единиц.
 * @param soldCartQuantity Количество проданных по карте единиц.
 * @param revenue Выручка от продаж за наличные (цена * soldCashQuantity).
 */
data class FastReportDomain(
    val productName: String,
    val productPrice: Double,
    val addedQuantity: Int,
    val replenishedQuantity: Int,
    val soldCashQuantity: Int,
    val soldCartQuantity: Int,
    val revenue: Double,
)
