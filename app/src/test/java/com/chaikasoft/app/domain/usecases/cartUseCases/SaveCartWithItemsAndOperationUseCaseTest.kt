package com.chaikasoft.app.domain.usecases.cartUseCases

import com.chaikasoft.app.data.inMemory.InMemoryCartRepositoryInterface
import com.chaikasoft.app.data.room.repo.RoomCartRepositoryInterface
import com.chaikasoft.app.domain.models.CartDomain
import com.chaikasoft.app.domain.models.CartItemDomain
import com.chaikasoft.app.domain.models.CartOperationDomain
import com.chaikasoft.app.domain.models.OperationTypeDomain
import com.chaikasoft.app.domain.models.ProductInfoDomain
import com.chaikasoft.app.domain.sealed.SaveOperationResult
import com.chaikasoft.app.domain.usecases.SaveCartWithItemsAndOperationUseCase
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

class SaveCartWithItemsAndOperationUseCaseTest : FunSpec({

    lateinit var repository: RoomCartRepositoryInterface
    lateinit var cart: InMemoryCartRepositoryInterface
    lateinit var useCase: SaveCartWithItemsAndOperationUseCase

    val product = ProductInfoDomain(
        id = 101,
        name = "Tea",
        description = "Black tea",
        image = "tea.png",
        price = 120
    )
    val item = CartItemDomain(product, quantity = 2)
    val operation = CartOperationDomain(OperationTypeDomain.ADD, conductorId = 7)

    // Аналог @BeforeEach: выполняется перед каждым test(...)
    beforeTest {
        repository = mockk()
        cart = mockk()
        useCase = SaveCartWithItemsAndOperationUseCase(repository)
    }

    /**
     * Техника тест-дизайна: #2 Граничные значения
     *
     * Описание:
     *   - Граница по данным корзины: список позиций пуст.
     *   - Ожидаемое поведение:
     *       1) возвращается EmptyCart,
     *       2) сохранение в БД и очистка корзины не вызываются.
     *   - Цель: зафиксировать инвариант "пустая корзина не сохраняется".
     */
    test("when cart is empty - returns EmptyCart and skips save and clear") {
        runTest {
            every { cart.getCartItems() } returns flowOf(emptyList())

            val result = useCase(cart, operation)

            result shouldBe SaveOperationResult.EmptyCart

            verify(exactly = 1) { cart.getCartItems() }
            coVerify(exactly = 0) { repository.saveCartWithItemsAndOperation(any(), any()) }
            verify(exactly = 0) { cart.clearCart() }
            confirmVerified(cart, repository)
        }
    }

    /**
     * Техника тест-дизайна: #3 Таблица решений (Decision Table)
     *
     * Описание:
     *   - Комбинация условий: items != empty, repo = success.
     *   - Ожидаемое поведение:
     *       1) корзина и операция сохраняются в БД,
     *       2) корзина очищается после сохранения,
     *       3) возвращается Success(opId).
     *   - Цель: зафиксировать сценарий успешного сохранения.
     */
    test("when cart has items and repo succeeds - saves, clears, and returns Success") {
        runTest {
            val items = listOf(item)
            val cartSlot = slot<CartDomain>()
            val operationSlot = slot<CartOperationDomain>()
            val opId = 42
            every { cart.getCartItems() } returns flowOf(items)
            coEvery {
                repository.saveCartWithItemsAndOperation(
                    capture(cartSlot),
                    capture(operationSlot)
                )
            } returns opId
            justRun { cart.clearCart() }

            val result = useCase(cart, operation)

            result shouldBe SaveOperationResult.Success(opId)
            cartSlot.captured.items shouldBe items
            operationSlot.captured shouldBe operation

            verify(exactly = 1) { cart.getCartItems() }
            coVerify(exactly = 1) { repository.saveCartWithItemsAndOperation(any(), any()) }
            verify(exactly = 1) { cart.clearCart() }
            // Порядок важен: чистим корзину только после успешного сохранения.
            coVerifyOrder {
                repository.saveCartWithItemsAndOperation(any(), any())
                cart.clearCart()
            }
            confirmVerified(cart, repository)
        }
    }

    /**
     * Техника тест-дизайна: #5 Error Guessing / Анализ типичных ошибок
     *
     * Описание:
     *   - Сценарий: репозиторий падает с исключением.
     *   - Ожидаемое поведение:
     *       1) возвращается Failure,
     *       2) корзина не очищается.
     *   - Цель: зафиксировать корректную обработку ошибок без потери данных корзины.
     */
    test("when repo throws - returns Failure and does not clear cart") {
        runTest {
            val items = listOf(item)
            val error = IllegalStateException("boom")
            every { cart.getCartItems() } returns flowOf(items)
            coEvery { repository.saveCartWithItemsAndOperation(any(), any()) } throws error

            val result = useCase(cart, operation)

            result shouldBe SaveOperationResult.Failure("boom")

            verify(exactly = 1) { cart.getCartItems() }
            coVerify(exactly = 1) { repository.saveCartWithItemsAndOperation(any(), any()) }
            verify(exactly = 0) { cart.clearCart() }
            confirmVerified(cart, repository)
        }
    }
})
