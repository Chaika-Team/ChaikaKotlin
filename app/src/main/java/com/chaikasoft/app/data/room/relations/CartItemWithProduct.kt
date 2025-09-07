package com.chaikasoft.app.data.room.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.chaikasoft.app.data.room.entities.CartItem
import com.chaikasoft.app.data.room.entities.ProductInfo

data class CartItemWithProduct(
    @Embedded val item: CartItem,
    @Relation(
        parentColumn = "product_id",
        entityColumn = "id"
    )
    val product: ProductInfo
)
