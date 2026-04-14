package com.chaikasoft.app.domain.usecases.templatesUseCases

import com.chaikasoft.app.data.inmemory.InMemoryCartRepositoryInterface
import com.chaikasoft.app.data.room.repo.RoomProductInfoRepositoryInterface
import com.chaikasoft.app.domain.models.CartItemDomain
import com.chaikasoft.app.domain.models.ProductInfoDomain
import com.chaikasoft.app.domain.models.TemplateContentDomain
import com.chaikasoft.app.domain.models.TemplateDomain
import com.chaikasoft.app.domain.usecases.ApplyTemplateUseCase
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import kotlinx.coroutines.test.runTest

class ApplyTemplateUseCaseTest : FunSpec({

    lateinit var productRepository: RoomProductInfoRepositoryInterface
    lateinit var cart: InMemoryCartRepositoryInterface
    lateinit var useCase: ApplyTemplateUseCase

    val productTea = ProductInfoDomain(
        id = 10,
        name = "Tea",
        description = "Black tea",
        image = "tea.png",
        price = 120
    )
    val productCoffee = ProductInfoDomain(
        id = 20,
        name = "Coffee",
        description = "Ground coffee",
        image = "coffee.png",
        price = 250
    )

    // Analog of @BeforeEach: runs before each test(...)
    beforeTest {
        productRepository = mockk()
        cart = mockk()
        useCase = ApplyTemplateUseCase(productRepository)
    }

    /**
     * Test design technique: #3 Decision Table
     *
     * Description:
     *   - Conditions: template has content, all products exist in DB.
     *   - Expected behavior:
     *       1) cart is cleared,
     *       2) each content item is mapped to CartItemDomain and added,
     *       3) clearCart is called before the first add (order).
     *   - Goal: validate the happy path of applying a template to the cart.
     */
    test("when products exist for all template items - clears cart and adds mapped items") {
        runTest {
            val template = TemplateDomain(
                id = 1,
                templateName = "Tea set",
                description = "Basic set",
                content = listOf(
                    TemplateContentDomain(productId = productTea.id, quantity = 2),
                    TemplateContentDomain(productId = productCoffee.id, quantity = 1)
                )
            )
            val addedItems = mutableListOf<CartItemDomain>()
            justRun { cart.clearCart() }
            every { cart.addItemToCart(any()) } returns true
            coEvery { productRepository.getProductById(productTea.id) } returns productTea
            coEvery { productRepository.getProductById(productCoffee.id) } returns productCoffee

            useCase(cart, template)

            verifyOrder {
                cart.clearCart()
                cart.addItemToCart(any())
                cart.addItemToCart(any())
            }
            verify(exactly = 2) { cart.addItemToCart(capture(addedItems)) }
            addedItems.sortedBy { it.product.id } shouldBe listOf(
                CartItemDomain(productTea, quantity = 2),
                CartItemDomain(productCoffee, quantity = 1)
            ).sortedBy { it.product.id }
            coVerify(exactly = 1) { productRepository.getProductById(productTea.id) }
            coVerify(exactly = 1) { productRepository.getProductById(productCoffee.id) }
            confirmVerified(cart, productRepository)
        }
    }

    /**
     * Test design technique: #5 Error Guessing / Common failure analysis
     *
     * Description:
     *   - Scenario: a product is missing in local DB.
     *   - Expected behavior:
     *       1) IllegalArgumentException is thrown,
     *       2) no items are added to the cart.
     *   - Goal: guard against silent template application with missing products.
     */
    test("when any product is missing - throws IllegalArgumentException and skips adds") {
        runTest {
            val missingId = 999
            val template = TemplateDomain(
                id = 2,
                templateName = "Broken set",
                description = "Missing product",
                content = listOf(
                    TemplateContentDomain(productId = missingId, quantity = 1)
                )
            )
            justRun { cart.clearCart() }
            coEvery { productRepository.getProductById(missingId) } returns null

            shouldThrow<IllegalArgumentException> {
                useCase(cart, template)
            }

            verify(exactly = 1) { cart.clearCart() }
            verify(exactly = 0) { cart.addItemToCart(any()) }
            coVerify(exactly = 1) { productRepository.getProductById(missingId) }
            confirmVerified(cart, productRepository)
        }
    }
})
