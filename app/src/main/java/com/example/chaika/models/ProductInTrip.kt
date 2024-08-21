package com.example.chaika.models

data class ProductInTrip(
    val id: Int,
    val title: String,
    val price: Double,
    val added: Int,
    val boughtCash: Int,
    val boughtCard: Int,
    val replenished: Int
)
