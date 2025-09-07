package com.chaikasoft.app.data.room.mappers

import com.chaikasoft.app.data.room.entities.CartItem
import com.chaikasoft.app.data.room.entities.ProductInfo
import com.chaikasoft.app.domain.models.CartItemDomain
import com.chaikasoft.app.domain.models.OperationTypeDomain
import com.chaikasoft.app.domain.models.report.CartItemReport

fun CartItem.toDomain(productInfo: ProductInfo): CartItemDomain =
    CartItemDomain(
        product = productInfo.toDomain(),
        quantity = this.impact,
    )

fun CartItemDomain.toEntity(
    cartOperationId: Int,
    operationTypeDomain: OperationTypeDomain,
): CartItem {
    // Преобразуем quantity в отрицательное значение при необходимости
    val impact =
        if (operationTypeDomain == OperationTypeDomain.SOLD_CASH || operationTypeDomain == OperationTypeDomain.SOLD_CART) {
            -this.quantity
        } else {
            this.quantity
        }

    return CartItem(
        id = 0,
        cartOperationId = cartOperationId, // Используем cartOperationId вместо cartId
        productId = this.product.id,
        impact = impact,
    )
}

// Репорт-модель для отчётов
fun CartItem.toReport(productInfo: ProductInfo): CartItemReport =
    CartItemReport(
        productId = this.productId,
        quantity = this.impact,
        price = productInfo.price,
    )
