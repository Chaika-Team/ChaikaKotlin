package com.chaikasoft.app.domain.usecases.cartUseCases

import com.chaikasoft.app.data.inMemory.InMemoryCartRepositoryInterface
import com.chaikasoft.app.domain.usecases.GetAvailableQuantityUseCase
import com.chaikasoft.app.domain.usecases.UpdateQuantityWithLimitUseCase
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest

class UpdateQuantityWithLimitUseCaseTest : FunSpec({

    lateinit var getAvailableQuantity: GetAvailableQuantityUseCase
    lateinit var cart: InMemoryCartRepositoryInterface
    lateinit var useCase: UpdateQuantityWithLimitUseCase

    // Аналог @BeforeEach: выполняется перед каждым test(...)
    beforeTest {
        getAvailableQuantity = mockk()
        cart = mockk()
        useCase = UpdateQuantityWithLimitUseCase(getAvailableQuantity)
    }

    /**
     * Техника тест-дизайна: #3 Таблица решений (Decision Table)
     *
     * Описание:
     *   - Комбинация условий: available = N, updateItemQuantity = true.
     *   - Ожидаемое поведение:
     *       1) доступный остаток запрашивается,
     *       2) cart.updateItemQuantity вызывается с N,
     *       3) результат пробрасывается наружу.
     *   - Цель: зафиксировать корректное прокидывание лимита в update.
     */
    test("when available is returned - passes it to cart and returns update result") {
        runTest {
            val itemId = 77
            val newQuantity = 5
            val available = 3
            coEvery { getAvailableQuantity(itemId) } returns available
            every { cart.updateItemQuantity(itemId, newQuantity, available) } returns true

            val result = useCase(cart, itemId, newQuantity)

            result shouldBe true

            coVerify(exactly = 1) { getAvailableQuantity(itemId) }
            verify(exactly = 1) { cart.updateItemQuantity(itemId, newQuantity, available) }
            confirmVerified(getAvailableQuantity, cart)
        }
    }
})
