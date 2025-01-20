package com.example.chaika.data.room.entities

import androidx.room.ColumnInfo
import androidx.room.DatabaseView

/**
 * Представление для быстрого отчёта о продажах.
 *
 * @param productName Название продукта.
 * @param productPrice Цена продукта.
 * @param addedQuantity Количество добавленных единиц продукта.
 * @param replenishedQuantity Количество пополненных единиц продукта.
 * @param soldCashQuantity Количество проданных за наличные единиц продукта.
 * @param soldCartQuantity Количество проданных по карте единиц продукта.
 * @param revenue Выручка от продаж за наличные.
 */
@DatabaseView(
    viewName = "fast_report_view",
    value = """
        SELECT 
            product_info.name AS product_name,
            product_info.price AS product_price,
            SUM(CASE WHEN cart_operations.operation_type = 0 THEN cart_items.impact ELSE 0 END) AS added_quantity,
            SUM(CASE WHEN cart_operations.operation_type = 3 THEN cart_items.impact ELSE 0 END) AS replenished_quantity,
            SUM(CASE WHEN cart_operations.operation_type = 1 THEN -cart_items.impact ELSE 0 END) AS sold_cash_quantity,
            SUM(CASE WHEN cart_operations.operation_type = 2 THEN -cart_items.impact ELSE 0 END) AS sold_cart_quantity,
            product_info.price * SUM(CASE WHEN cart_operations.operation_type = 1 THEN -cart_items.impact ELSE 0 END) AS revenue
        FROM cart_items
        JOIN cart_operations ON cart_operations.id = cart_items.cart_operation_id
        JOIN product_info ON product_info.id = cart_items.product_id
        GROUP BY product_info.id
    """
)
data class FastReportView(
    @ColumnInfo(name = "product_name") val productName: String,
    @ColumnInfo(name = "product_price") val productPrice: Double,
    @ColumnInfo(name = "added_quantity") val addedQuantity: Int,
    @ColumnInfo(name = "replenished_quantity") val replenishedQuantity: Int,
    @ColumnInfo(name = "sold_cash_quantity") val soldCashQuantity: Int,
    @ColumnInfo(name = "sold_cart_quantity") val soldCartQuantity: Int,
    @ColumnInfo(name = "revenue") val revenue: Double
)
