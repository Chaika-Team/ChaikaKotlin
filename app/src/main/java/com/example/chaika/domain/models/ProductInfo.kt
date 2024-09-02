package com.example.chaika.domain.models

data class ProductInfo(
    val id: Int,
    val name: String,
    val description: String,
    val image: String,
    val price: Double // в таблице должен быть REAL
)