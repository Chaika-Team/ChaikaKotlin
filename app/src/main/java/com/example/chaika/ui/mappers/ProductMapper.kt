package com.example.chaika.ui.mappers

import com.example.chaika.domain.models.CartItemDomain
import com.example.chaika.domain.models.ProductInfoDomain
import com.example.chaika.ui.dto.Product

fun Product.toDomain(): ProductInfoDomain = ProductInfoDomain(
    id = this.id,
    name = this.name,
    description = this.description,
    image = this.image,
    price = this.price
)

fun ProductInfoDomain.toUiModel(): Product {
    return Product(
        id = this.id,
        name = this.name,
        description = this.description,
        image = this.image,
        price = this.price,
        isInCart = false,
        quantity = 1,
        isInPackage = false,
        packageQuantity = 1
    )
}

fun Product.toCartItemDomain(): CartItemDomain = CartItemDomain(
    product = this.toDomain(),
    quantity = this.quantity
)

fun CartItemDomain.toUiModel(): Product {
    return Product(
        id = this.product.id,
        name = this.product.name,
        description = this.product.description,
        image = this.product.image,
        price = this.product.price,
        isInCart = true,
        quantity = this.quantity,
        isInPackage = false,
        packageQuantity = 0
    )
}

fun ProductInfoDomain.toCartItemDomain(): CartItemDomain {
    return CartItemDomain(
        product = this,
        quantity = 1
    )
}