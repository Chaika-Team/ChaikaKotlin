package com.example.chaika.data.room.mappers

import com.example.chaika.data.room.entities.CartItem as CartItemEntity
import com.example.chaika.domain.models.CartItem as CartItemDomain
import com.example.chaika.domain.models.ProductInfo as ProductInfoDomain
import com.example.chaika.data.room.entities.ProductInfo as ProductInfoEntity
import com.example.chaika.domain.models.OperationType

fun CartItemEntity.toDomain(productInfoEntity: ProductInfoEntity): CartItemDomain {
    return CartItemDomain(
        product = productInfoEntity.toDomain(),
        quantity = this.impact
    )
}

fun CartItemDomain.toEntity(cartOperationId: Int, operationType: OperationType): CartItemEntity {
    // Преобразуем quantity в отрицательное значение при необходимости
    val impact = if (operationType == OperationType.BROUGHT_CASH || operationType == OperationType.BROUGHT_CART) {
        -this.quantity
    } else {
        this.quantity
    }

    return CartItemEntity(
        cartOperationId = cartOperationId, // Используем cartOperationId вместо cartId
        productId = this.product.id,
        impact = impact
    )
}
