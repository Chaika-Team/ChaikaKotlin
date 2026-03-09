package com.chaikasoft.app.data.room.mappers

import com.chaikasoft.app.data.room.entities.CartItem
import com.chaikasoft.app.data.room.entities.ProductInfo
import com.chaikasoft.app.data.room.relations.CartItemWithProduct
import com.chaikasoft.app.domain.models.CartDomain
import com.chaikasoft.app.domain.models.CartItemDomain
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class OperationItemsMapperTest : FunSpec({

    /**
     * Test design: equivalence classes.
     * Relation list should map 1:1 into cart domain items.
     */
    test("toCartDomain maps list of relation items to CartDomain") {
        val list = listOf(
            CartItemWithProduct(
                item = CartItem(id = 1, cartOperationId = 9, productId = 100, impact = 2),
                product = ProductInfo(id = 100, name = "Tea", description = "Black", image = "img", price = 150),
            ),
            CartItemWithProduct(
                item = CartItem(id = 2, cartOperationId = 9, productId = 200, impact = -1),
                product = ProductInfo(id = 200, name = "Coffee", description = "Arabica", image = "img2", price = 220),
            ),
        )

        val domain = list.toCartDomain()

        domain shouldBe CartDomain(
            items = listOf(
                CartItemDomain(product = list[0].product.toDomain(), quantity = 2),
                CartItemDomain(product = list[1].product.toDomain(), quantity = -1),
            ),
        )
    }
})

