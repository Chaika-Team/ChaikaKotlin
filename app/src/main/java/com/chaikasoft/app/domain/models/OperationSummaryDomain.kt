package com.chaikasoft.app.domain.models

data class OperationSummaryDomain(
    val id: Int,
    val type: OperationTypeDomain,
    val timeIso: String,
    val conductor: ConductorDomain,
    // число РАЗНЫХ товаров в операции
    val productLineQuantity: Int,
    // сумма |impact| * price (для быстрого отображения)
    val totalPrice: Int
)
