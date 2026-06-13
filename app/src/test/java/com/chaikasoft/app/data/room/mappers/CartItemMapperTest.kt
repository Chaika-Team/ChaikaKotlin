package com.chaikasoft.app.data.room.mappers

import com.chaikasoft.app.data.room.entities.CartItem
import com.chaikasoft.app.data.room.entities.ProductInfo
import com.chaikasoft.app.domain.models.CartItemDomain
import com.chaikasoft.app.domain.models.OperationTypeDomain
import com.chaikasoft.app.domain.models.ProductInfoDomain
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class CartItemMapperTest : FunSpec({

    /**
     * Test design: decision table.
     * We lock sign rules for impact by operation type to prevent regressions.
     */
    test("toEntity uses negative impact for sold operations and positive for others") {
        val domain = CartItemDomain(
            product = ProductInfoDomain(10, "Tea", "Black", "img", 150),
            quantity = 3,
        )

        domain.toEntity(cartOperationId = 1, operationTypeDomain = OperationTypeDomain.SOLD_CASH).impact shouldBe -3
        domain.toEntity(cartOperationId = 1, operationTypeDomain = OperationTypeDomain.SOLD_CARD).impact shouldBe -3
        domain.toEntity(cartOperationId = 1, operationTypeDomain = OperationTypeDomain.ADD).impact shouldBe 3
        domain.toEntity(cartOperationId = 1, operationTypeDomain = OperationTypeDomain.REPLENISH).impact shouldBe 3
    }

    /**
     * Test design: equivalence classes.
     * toEntity must always create new row model with id=0 and mapped fields.
     */
    test("toEntity maps all fields and always sets id to zero") {
        val domain = CartItemDomain(
            product = ProductInfoDomain(99, "Cake", "Sweet", "img", 250),
            quantity = 4,
        )

        val entity = domain.toEntity(
            cartOperationId = 77,
            operationTypeDomain = OperationTypeDomain.ADD,
        )

        entity shouldBe CartItem(
            id = 0,
            cartOperationId = 77,
            productId = 99,
            impact = 4,
        )
    }

    /**
     * Test design: equivalence classes.
     * Entity/domain/report mapping should preserve quantity and price semantics.
     */
    test("toDomain and toReport map quantity and price correctly") {
        val entity = CartItem(
            id = 1,
            cartOperationId = 2,
            productId = 10,
            impact = -5,
        )
        val product = ProductInfo(
            id = 10,
            name = "Coffee",
            description = "Arabica",
            image = "img",
            price = 120,
        )

        entity.toDomain(product) shouldBe CartItemDomain(
            product = product.toDomain(),
            quantity = -5,
        )
        entity.toReport(product).productId shouldBe 10
        entity.toReport(product).quantity shouldBe -5
        entity.toReport(product).price shouldBe 120
    }
})

