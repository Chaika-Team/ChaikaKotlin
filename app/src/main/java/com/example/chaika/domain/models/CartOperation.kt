package com.example.chaika.domain.models

data class CartOperation(
    val operationType: OperationType, // Enum class
    val conductorId: Int
)

enum class OperationType {
    ADD, BROUGHT_CASH, BROUGHT_CART, REPLENISH
}