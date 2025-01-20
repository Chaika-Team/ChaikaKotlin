package com.example.chaika.domain.models

/**
 * Доменная модель операции с корзиной.
 *
 * @param operationTypeDomain Тип операции (ADD, SOLD_CASH, SOLD_CART, REPLENISH).
 * @param conductorId Идентификатор проводника, связанного с операцией.
 */
data class CartOperationDomain(
    val operationTypeDomain: OperationTypeDomain,
    val conductorId: Int
)

/**
 * Перечисление типов операций с корзиной.
 *
 * ADD - Добавление товаров.
 * SOLD_CASH - Продажа за наличные.
 * SOLD_CART - Продажа по карте.
 * REPLENISH - Пополнение.
 */
enum class OperationTypeDomain {
    ADD, SOLD_CASH, SOLD_CART, REPLENISH
}
