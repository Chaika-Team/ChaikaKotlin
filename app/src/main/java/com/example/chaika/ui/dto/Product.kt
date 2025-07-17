package com.example.chaika.ui.dto

import com.example.chaika.domain.models.ProductInfoDomain


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
) {
    fun toDomain(): ProductInfoDomain = ProductInfoDomain(
        id = this.id,
        name = this.name,
        description = this.description,
        image = this.image,
        price = this.price
    )
}