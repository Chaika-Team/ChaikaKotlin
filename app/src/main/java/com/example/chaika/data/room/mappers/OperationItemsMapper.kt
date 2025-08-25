package com.example.chaika.data.room.mappers

import com.example.chaika.data.room.relations.CartItemWithProduct
import com.example.chaika.domain.models.CartDomain
import com.example.chaika.domain.models.CartItemDomain

fun List<CartItemWithProduct>.toCartDomain(): CartDomain =
    CartDomain(
        items = map {
            CartItemDomain(
                product = it.product.toDomain(), // у вас уже есть ProductInfo.toDomain()
                quantity = it.item.impact
            )
        }
    )
