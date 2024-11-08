package com.example.chaika.domain.models

data class CartItemDomain(
    val product: ProductInfoDomain, // Теперь используем ProductInfo вместо productId
    var quantity: Int
)
