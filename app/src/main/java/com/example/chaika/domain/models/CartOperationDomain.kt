package com.example.chaika.domain.models

data class CartOperationDomain(
    val operationTypeDomain: OperationTypeDomain, // Enum class
    val conductorId: Int
)

enum class OperationTypeDomain {
    ADD, SOLD_CASH, SOLD_CART, REPLENISH
}