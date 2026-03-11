package com.chaikasoft.app.data.room.mappers

import com.chaikasoft.app.data.room.entities.PackageItemView
import com.chaikasoft.app.domain.models.PackageItemDomain
import com.chaikasoft.app.domain.models.ProductInfoDomain
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class PackageItemViewMapperTest : FunSpec({

    /**
     * Test design: equivalence classes.
     * Package item view should compose with provided product info into domain.
     */
    test("toDomain maps view and injected product info") {
        val view = PackageItemView(
            productId = 7,
            currentQuantity = 12,
        )
        val product = ProductInfoDomain(
            id = 7,
            name = "Water",
            description = "Still",
            image = "img",
            price = 50,
        )

        view.toDomain(product) shouldBe PackageItemDomain(
            productInfoDomain = product,
            currentQuantity = 12,
        )
    }
})

