package com.chaikasoft.app.domain.usecases.cartUseCases

import com.chaikasoft.app.data.inmemory.InMemoryCartRepositoryInterface
import com.chaikasoft.app.domain.models.CartItemDomain
import com.chaikasoft.app.domain.models.ProductInfoDomain
import com.chaikasoft.app.domain.sealed.AddItemToCartWithLimitResult
import com.chaikasoft.app.domain.usecases.AddItemToCartWithLimitUseCase
import com.chaikasoft.app.domain.usecases.GetAvailableQuantityUseCase
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest

class AddItemToCartWithLimitUseCaseTest : FunSpec({

    lateinit var getAvailableQuantity: GetAvailableQuantityUseCase
    lateinit var cart: InMemoryCartRepositoryInterface
    lateinit var useCase: AddItemToCartWithLimitUseCase

    val product = ProductInfoDomain(
        id = 501,
        name = "Coffee",
        description = "Ground coffee",
        image = "coffee.png",
        price = 250
    )
    val item = CartItemDomain(product, quantity = 1)

    // Аналог @BeforeEach: выполняется перед каждым test(...)
    beforeTest {
        getAvailableQuantity = mockk()
        cart = mockk()
        useCase = AddItemToCartWithLimitUseCase(getAvailableQuantity)
    }

    /**
     * Техника тест-дизайна: #2 Граничные значения
     *
     * Описание:
     *   - Граница по available: 0 (нет доступного остатка).
     *   - Ожидаемое поведение:
     *       1) возвращается OutOfStock,
     *       2) добавление в корзину не вызывается.
     *   - Цель: зафиксировать запрет на добавление при нулевом остатке.
     */
    test("when available is zero or less - returns out of stock and does not add item") {
        runTest {
            coEvery { getAvailableQuantity(product.id) } returns 0

            val result = useCase(cart, item)

            result shouldBe AddItemToCartWithLimitResult.OutOfStock

            coVerify(exactly = 1) { getAvailableQuantity(product.id) }
            verify(exactly = 0) { cart.addItemToCart(any()) }
            confirmVerified(getAvailableQuantity, cart)
        }
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Описание:
     *   - Эквивалентный класс: available > 0.
     *   - Ожидаемое поведение:
     *       1) вызывается cart.addItemToCart(item),
     *       2) успешное добавление возвращается как Added.
     *   - Цель: зафиксировать корректное делегирование при наличии остатка.
     */
    test("when available is positive and cart accepts item - returns added") {
        runTest {
            coEvery { getAvailableQuantity(product.id) } returns 3
            every { cart.addItemToCart(item) } returns true

            val result = useCase(cart, item)

            result shouldBe AddItemToCartWithLimitResult.Added

            coVerify(exactly = 1) { getAvailableQuantity(product.id) }
            verify(exactly = 1) { cart.addItemToCart(item) }
            confirmVerified(getAvailableQuantity, cart)
        }
    }

    test("when available is positive and cart rejects duplicate - returns already in cart") {
        runTest {
            coEvery { getAvailableQuantity(product.id) } returns 3
            every { cart.addItemToCart(item) } returns false

            val result = useCase(cart, item)

            result shouldBe AddItemToCartWithLimitResult.AlreadyInCart

            coVerify(exactly = 1) { getAvailableQuantity(product.id) }
            verify(exactly = 1) { cart.addItemToCart(item) }
            confirmVerified(getAvailableQuantity, cart)
        }
    }
})
