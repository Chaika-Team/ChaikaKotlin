package com.chaikasoft.app.ui.mappers

import com.chaikasoft.app.ui.cartItem
import com.chaikasoft.app.ui.dto.Product
import com.chaikasoft.app.ui.packageItem
import com.chaikasoft.app.ui.productInfo
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ProductMapperTest : FunSpec({

    test("Product.toDomain maps fields one-to-one") {
        val ui = Product(
            id = 10,
            name = "Coffee",
            description = "desc",
            image = "image",
            price = 150,
            isInCart = true,
            quantity = 3
        )

        val domain = ui.toDomain()

        domain.id shouldBe 10
        domain.name shouldBe "Coffee"
        domain.description shouldBe "desc"
        domain.image shouldBe "image"
        domain.price shouldBe 150
    }

    test("ProductInfoDomain.toUiModel sets UI defaults") {
        val domain = productInfo(id = 1, price = 99)

        val ui = domain.toUiModel()

        ui.id shouldBe domain.id
        ui.isInCart shouldBe false
        ui.quantity shouldBe 1
    }

    test("CartItemDomain.toUiModel marks item as in-cart and preserves quantity") {
        val cartItem = cartItem(product = productInfo(id = 7), quantity = 5)

        val ui = cartItem.toUiModel()

        ui.id shouldBe 7
        ui.isInCart shouldBe true
        ui.quantity shouldBe 5
    }

    test("PackageItemDomain.toUiModel maps current quantity and marks as in-cart") {
        val packageItem = packageItem(product = productInfo(id = 12), quantity = 8)

        val ui = packageItem.toUiModel()

        ui.id shouldBe 12
        ui.isInCart shouldBe true
        ui.quantity shouldBe 8
    }

    test("toCartItemDomain from Product and ProductInfoDomain always sets quantity=1") {
        val product = Product(
            id = 11,
            name = "Tea",
            description = "desc",
            image = "img",
            price = 50
        )
        val info = productInfo(id = 22)

        product.toCartItemDomain().quantity shouldBe 1
        info.toCartItemDomain().quantity shouldBe 1
    }
})

