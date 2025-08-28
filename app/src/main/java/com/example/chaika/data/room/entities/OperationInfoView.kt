package com.example.chaika.data.room.entities

import androidx.room.ColumnInfo
import androidx.room.DatabaseView

/**
 * Представление «шапки» операции для быстрого листинга.
 *
 * @param operationId ID операции.
 * @param operationType Тип операции (Int).
 * @param operationTime Время операции (ISO 8601).
 * @param conductorId ID проводника.
 * @param conductorName Имя проводника.
 * @param conductorFamilyName Фамилия проводника.
 * @param conductorGivenName Отчество проводника.
 * @param productLineQuantity Количество разных товаров в операции.
 * @param totalPrice Общая стоимость (сумма |impact| * price).
 */
@DatabaseView(
    viewName = "operation_info_view",
    value = """
        SELECT
            o.id                          AS operation_id,
            o.operation_type              AS operation_type,
            o.operation_time              AS operation_time,
            o.conductor_id                AS conductor_id,
            c.name                        AS conductor_name,
            c.familyName                  AS conductor_family_name,
            c.givenName                   AS conductor_given_name,
            COALESCE(COUNT(DISTINCT ci.product_id), 0)                   AS product_line_quantity,
            COALESCE(SUM(ABS(ci.impact) * p.price), 0.0)                 AS total_price
        FROM cart_operations o
        LEFT JOIN conductors   c  ON c.id = o.conductor_id
        LEFT JOIN cart_items   ci ON ci.cart_operation_id = o.id
        LEFT JOIN product_info p  ON p.id = ci.product_id
        GROUP BY o.id
    """
)
data class OperationInfoView(
    @ColumnInfo(name = "operation_id") val operationId: Int,
    @ColumnInfo(name = "operation_type") val operationType: Int,
    @ColumnInfo(name = "operation_time") val operationTime: String,
    @ColumnInfo(name = "conductor_id") val conductorId: Int,
    @ColumnInfo(name = "conductor_name") val conductorName: String,
    @ColumnInfo(name = "conductor_family_name") val conductorFamilyName: String,
    @ColumnInfo(name = "conductor_given_name") val conductorGivenName: String,
    @ColumnInfo(name = "product_line_quantity") val productLineQuantity: Int,
    @ColumnInfo(name = "total_price") val totalPrice: Double,
)
