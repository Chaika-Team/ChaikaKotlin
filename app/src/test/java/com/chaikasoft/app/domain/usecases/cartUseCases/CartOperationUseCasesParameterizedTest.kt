package com.chaikasoft.app.domain.usecases.cartUseCases

import com.chaikasoft.app.data.inmemory.InMemoryCartRepositoryInterface
import com.chaikasoft.app.domain.models.CartOperationDomain
import com.chaikasoft.app.domain.models.OperationTypeDomain
import com.chaikasoft.app.domain.sealed.SaveOperationResult
import com.chaikasoft.app.domain.usecases.AddOpUseCase
import com.chaikasoft.app.domain.usecases.ReplenishUseCase
import com.chaikasoft.app.domain.usecases.SaveCartWithItemsAndOperationUseCase
import com.chaikasoft.app.domain.usecases.SoldCardOpUseCase
import com.chaikasoft.app.domain.usecases.SoldCashOpUseCase
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest

class CartOperationUseCasesParameterizedTest : FunSpec({

    data class Case(
        val name: String,
        val expectedType: OperationTypeDomain,
        val invoke:
            suspend (
                SaveCartWithItemsAndOperationUseCase,
                InMemoryCartRepositoryInterface,
                Int,
            ) -> SaveOperationResult,
    )

    val cases =
        listOf(
            Case(
                name = "AddOpUseCase",
                expectedType = OperationTypeDomain.ADD,
                invoke = { saveOp, cart, conductorId -> AddOpUseCase(saveOp)(cart, conductorId) },
            ),
            Case(
                name = "SoldCashOpUseCase",
                expectedType = OperationTypeDomain.SOLD_CASH,
                invoke = { saveOp, cart, conductorId -> SoldCashOpUseCase(saveOp)(cart, conductorId) },
            ),
            Case(
                name = "SoldCardOpUseCase",
                expectedType = OperationTypeDomain.SOLD_CARD,
                invoke = { saveOp, cart, conductorId -> SoldCardOpUseCase(saveOp)(cart, conductorId) },
            ),
            Case(
                name = "ReplenishUseCase",
                expectedType = OperationTypeDomain.REPLENISH,
                invoke = { saveOp, cart, conductorId -> ReplenishUseCase(saveOp)(cart, conductorId) },
            ),
        )

    cases.forEach { case ->
        test("${case.name}: delegates to saveOp with ${case.expectedType}") {
            runTest {
                val saveOp = mockk<SaveCartWithItemsAndOperationUseCase>()
                val cart = mockk<InMemoryCartRepositoryInterface>()
                val conductorId = 42
                val operationSlot = slot<CartOperationDomain>()
                val expected = SaveOperationResult.Success(11)

                coEvery { saveOp(cart, capture(operationSlot)) } returns expected

                val result = case.invoke(saveOp, cart, conductorId)

                result shouldBe expected
                operationSlot.captured.operationTypeDomain shouldBe case.expectedType
                operationSlot.captured.conductorId shouldBe conductorId
                coVerify(exactly = 1) { saveOp(cart, any()) }
            }
        }
    }
})
