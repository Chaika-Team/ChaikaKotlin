package com.example.chaika.ui.dto

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val image: String,
    val price: Double,
    val isInCart: Boolean = false,
    val quantity: Int = 1,
    val isInPackage: Boolean = false,
    val packageQuantity: Int = 1
)