package com.example.chaika.domain.models

data class OperationSummaryDomain(
    val id: Int,
    val type: OperationTypeDomain,
    val timeIso: String,
    val conductor: ConductorDomain,
    val productLineQuantity: Int, // <-- число РАЗНЫХ товаров в операции
    val totalPrice: Double        // сумма |impact| * price (для быстрого отображения)
)
