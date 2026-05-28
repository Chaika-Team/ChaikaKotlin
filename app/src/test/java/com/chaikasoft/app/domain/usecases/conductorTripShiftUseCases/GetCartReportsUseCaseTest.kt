package com.chaikasoft.app.domain.usecases.conductorTripShiftUseCases

import com.chaikasoft.app.data.room.repo.RoomCartItemRepositoryInterface
import com.chaikasoft.app.data.room.repo.RoomCartOperationRepositoryInterface
import com.chaikasoft.app.domain.models.report.CartIdReport
import com.chaikasoft.app.domain.models.report.CartItemReport
import com.chaikasoft.app.domain.models.report.CartOperationReportHeader
import com.chaikasoft.app.domain.models.report.CartReport
import com.chaikasoft.app.domain.usecases.GetCartReportsUseCase
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

class GetCartReportsUseCaseTest : FunSpec({

    lateinit var cartOpRepo: RoomCartOperationRepositoryInterface
    lateinit var cartItemRepo: RoomCartItemRepositoryInterface
    lateinit var useCase: GetCartReportsUseCase

    beforeTest {
        cartOpRepo = mockk()
        cartItemRepo = mockk()
        useCase = GetCartReportsUseCase(cartOpRepo, cartItemRepo)
    }

    test("when no operations - returns empty list and does not query items") {
        runTest {
            every { cartOpRepo.getCartOperationReportHeadersWithIds() } returns flowOf(emptyList())

            val result = useCase()

            result shouldBe emptyList<CartReport>()

            verify(exactly = 1) { cartOpRepo.getCartOperationReportHeadersWithIds() }
            verify(exactly = 0) { cartItemRepo.getCartItemReportsByOperationId(any()) }
            confirmVerified(cartOpRepo, cartItemRepo)
        }
    }

    test("when operations exist - maps each op and items into cart reports") {
        runTest {
            val firstId = CartIdReport(
                employeeId = "emp-01",
                operationTime = "2025-01-01T01:00:00Z"
            )
            val secondId = CartIdReport(
                employeeId = "emp-02",
                operationTime = "2025-01-01T02:00:00Z"
            )
            val ops = listOf(
                10 to CartOperationReportHeader(cartId = firstId, operationType = 1),
                20 to CartOperationReportHeader(cartId = secondId, operationType = 2)
            )
            val itemsForFirst = listOf(
                CartItemReport(productId = 101, quantity = 2, price = 150)
            )
            val itemsForSecond = listOf(
                CartItemReport(productId = 202, quantity = 1, price = 300)
            )

            every { cartOpRepo.getCartOperationReportHeadersWithIds() } returns flowOf(ops)
            every { cartItemRepo.getCartItemReportsByOperationId(10) } returns flowOf(itemsForFirst)
            every { cartItemRepo.getCartItemReportsByOperationId(20) } returns flowOf(itemsForSecond)

            val result = useCase()

            val expected = listOf(
                CartReport(
                    cartId = firstId,
                    operationType = 1,
                    items = itemsForFirst
                ),
                CartReport(
                    cartId = secondId,
                    operationType = 2,
                    items = itemsForSecond
                )
            )

            result shouldBe expected

            verify(exactly = 1) { cartOpRepo.getCartOperationReportHeadersWithIds() }
            verify(exactly = 1) { cartItemRepo.getCartItemReportsByOperationId(10) }
            verify(exactly = 1) { cartItemRepo.getCartItemReportsByOperationId(20) }
            confirmVerified(cartOpRepo, cartItemRepo)
        }
    }

    test("when operation has no items - returns cart report with empty items") {
        runTest {
            val cartId = CartIdReport(
                employeeId = "emp-10",
                operationTime = "2025-01-01T03:00:00Z"
            )
            val ops = listOf(
                30 to CartOperationReportHeader(cartId = cartId, operationType = 3)
            )

            every { cartOpRepo.getCartOperationReportHeadersWithIds() } returns flowOf(ops)
            every { cartItemRepo.getCartItemReportsByOperationId(30) } returns flowOf(emptyList())

            val result = useCase()

            val expected = listOf(
                CartReport(
                    cartId = cartId,
                    operationType = 3,
                    items = emptyList()
                )
            )

            result shouldBe expected

            verify(exactly = 1) { cartOpRepo.getCartOperationReportHeadersWithIds() }
            verify(exactly = 1) { cartItemRepo.getCartItemReportsByOperationId(30) }
            confirmVerified(cartOpRepo, cartItemRepo)
        }
    }
})
