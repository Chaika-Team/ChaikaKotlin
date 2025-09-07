package com.chaikasoft.app.data.room.mappers

import com.chaikasoft.app.data.room.relations.CartItemWithProduct
import com.chaikasoft.app.domain.models.CartDomain
import com.chaikasoft.app.domain.models.CartItemDomain

fun List<CartItemWithProduct>.toCartDomain(): CartDomain =
    CartDomain(
        items = map {
            CartItemDomain(
                product = it.product.toDomain(), // у вас уже есть ProductInfo.toDomain()
                quantity = it.item.impact
            )
        }
    )
