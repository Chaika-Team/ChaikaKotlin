package com.chaikasoft.app.data.room.mappers

import com.chaikasoft.app.data.room.entities.ProductInfo
import com.chaikasoft.app.domain.models.ProductInfoDomain
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ProductInfoMapperTest : FunSpec({

    /**
     * Test design: equivalence classes.
     * Product mapper is pure and must keep all fields in both directions.
     */
    test("product maps both ways without data loss") {
        val entity = ProductInfo(
            id = 5,
            name = "Chocolate",
            description = "Dark",
            image = "img",
            price = 300,
        )
        val domain = ProductInfoDomain(
            id = 5,
            name = "Chocolate",
            description = "Dark",
            image = "img",
            price = 300,
        )

        entity.toDomain() shouldBe domain
        domain.toEntity() shouldBe entity
    }
})

