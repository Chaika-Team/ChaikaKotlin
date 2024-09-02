package com.example.chaika.domain.models

data class CartItem(
    val product: ProductInfo, // Теперь используем ProductInfo вместо productId
    val quantity: Int
)
