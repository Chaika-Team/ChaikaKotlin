package com.example.chaika.domain.models

data class ProductInfoDomain(
    val id: Int,
    val name: String,
    val description: String,
    val image: String,
    val price: Double // в таблице лучше иметь REAL
)