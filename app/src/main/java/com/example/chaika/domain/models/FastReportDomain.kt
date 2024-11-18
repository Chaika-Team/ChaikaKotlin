package com.example.chaika.domain.models

data class FastReportDomain(
    val productName: String,
    val productPrice: Double,
    val addedQuantity: Int,
    val replenishedQuantity: Int,
    val soldCashQuantity: Int,
    val soldCartQuantity: Int,
    val revenue: Double
)
