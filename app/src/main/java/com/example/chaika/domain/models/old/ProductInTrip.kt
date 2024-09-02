package com.example.chaika.domain.models.old

data class ProductInTrip(
    val id: Int,
    val title: String,
    val price: Double,
    val added: Int,
    val boughtCash: Int,
    val boughtCard: Int,
    val replenished: Int
)
