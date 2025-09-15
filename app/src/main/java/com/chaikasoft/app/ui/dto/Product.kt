package com.chaikasoft.app.ui.dto

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val image: String,
    val price: Int,
    val isInCart: Boolean = false,
    val quantity: Int = 1,
)