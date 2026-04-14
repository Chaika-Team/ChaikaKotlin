package com.chaikasoft.app.ui.mappers

import com.chaikasoft.app.domain.models.CartItemDomain
import com.chaikasoft.app.domain.models.PackageItemDomain
import com.chaikasoft.app.domain.models.ProductInfoDomain
import com.chaikasoft.app.ui.dto.Product

fun Product.toDomain(): ProductInfoDomain = ProductInfoDomain(
    id = this.id,
    name = this.name,
    description = this.description,
    image = this.image,
    price = this.price
)

fun ProductInfoDomain.toUiModel(): Product = Product(
    id = this.id,
    name = this.name,
    description = this.description,
    image = this.image,
    price = this.price,
    isInCart = false,
    quantity = 1
)

fun Product.toCartItemDomain(): CartItemDomain = CartItemDomain(
    product = this.toDomain(),
    quantity = 1
)

fun CartItemDomain.toUiModel(): Product = Product(
    id = this.product.id,
    name = this.product.name,
    description = this.product.description,
    image = this.product.image,
    price = this.product.price,
    isInCart = true,
    quantity = this.quantity
)

fun ProductInfoDomain.toCartItemDomain(): CartItemDomain = CartItemDomain(
    product = this,
    quantity = 1
)

fun PackageItemDomain.toUiModel(): Product = Product(
    id = this.productInfoDomain.id,
    name = this.productInfoDomain.name,
    description = this.productInfoDomain.description,
    image = this.productInfoDomain.image,
    price = this.productInfoDomain.price,
    isInCart = true,
    quantity = this.currentQuantity
)
