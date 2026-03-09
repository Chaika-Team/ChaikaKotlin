package com.chaikasoft.app.domain.usecases.conductorTripShiftUseCases

import com.chaikasoft.app.data.room.repo.RoomCartItemRepositoryInterface
import com.chaikasoft.app.data.room.repo.RoomCartOperationRepositoryInterface
import com.chaikasoft.app.domain.models.report.CartIdReport
import com.chaikasoft.app.domain.models.report.CartItemReport
import com.chaikasoft.app.domain.models.report.CartOperationReport
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

    // Analog of @BeforeEach: runs before each test(...)
    beforeTest {
        cartOpRepo = mockk()
        cartItemRepo = mockk()
        useCase = GetCartReportsUseCase(cartOpRepo, cartItemRepo)
    }

    /**
     * Test-design technique: #2 Boundary values
     *
     * Author: OwletsFox
     *
     * Description:
     *   - Boundary case: no cart operations in storage.
     *   - Expected behavior:
     *       1) use case returns an empty list,
     *       2) does not request item reports by operation id.
     *   - Goal: avoid extra queries and ensure empty input maps to empty output.
     */
    test("when no operations - returns empty list and does not query items") {
        runTest {
            every { cartOpRepo.getCartOperationReportsWithIds() } returns flowOf(emptyList())

            val result = useCase()

            result shouldBe emptyList<CartReport>()

            verify(exactly = 1) { cartOpRepo.getCartOperationReportsWithIds() }
            verify(exactly = 0) { cartItemRepo.getCartItemReportsByOperationId(any()) }
            confirmVerified(cartOpRepo, cartItemRepo)
        }
    }

    /**
     * Test-design technique: #7 Decision table
     *
     * Author: OwletsFox
     *
     * Description:
     *   - Conditions: multiple operations exist and each has its own item list.
     *   - Expected behavior:
     *       1) use case maps each operation to CartReport,
     *       2) items are taken from cartItemRepo (not from opReport.items),
     *       3) item queries are executed once per operation id.
     *   - Goal: verify correct aggregation across multiple operations.
     */
    test("when operations exist - maps each op and items into cart reports") {
        runTest {
            val opReportFirst = CartOperationReport(
                employeeID = "emp-01",
                operationType = 1,
                operationTime = "2025-01-01T01:00:00Z",
                items = listOf(CartItemReport(productId = 999, quantity = 99, price = 999))
            )
            val opReportSecond = CartOperationReport(
                employeeID = "emp-02",
                operationType = 2,
                operationTime = "2025-01-01T02:00:00Z",
                items = listOf(CartItemReport(productId = 888, quantity = 88, price = 888))
            )
            val ops = listOf(10 to opReportFirst, 20 to opReportSecond)
            val itemsForFirst = listOf(
                CartItemReport(productId = 101, quantity = 2, price = 150)
            )
            val itemsForSecond = listOf(
                CartItemReport(productId = 202, quantity = 1, price = 300)
            )

            every { cartOpRepo.getCartOperationReportsWithIds() } returns flowOf(ops)
            every { cartItemRepo.getCartItemReportsByOperationId(10) } returns flowOf(itemsForFirst)
            every { cartItemRepo.getCartItemReportsByOperationId(20) } returns flowOf(itemsForSecond)

            val result = useCase()

            val expected = listOf(
                CartReport(
                    cartId = CartIdReport(
                        employeeId = "emp-01",
                        operationTime = "2025-01-01T01:00:00Z"
                    ),
                    operationType = 1,
                    items = itemsForFirst
                ),
                CartReport(
                    cartId = CartIdReport(
                        employeeId = "emp-02",
                        operationTime = "2025-01-01T02:00:00Z"
                    ),
                    operationType = 2,
                    items = itemsForSecond
                )
            )

            result shouldBe expected

            verify(exactly = 1) { cartOpRepo.getCartOperationReportsWithIds() }
            verify(exactly = 1) { cartItemRepo.getCartItemReportsByOperationId(10) }
            verify(exactly = 1) { cartItemRepo.getCartItemReportsByOperationId(20) }
            confirmVerified(cartOpRepo, cartItemRepo)
        }
    }

    /**
     * Test-design technique: #2 Boundary values
     *
     * Author: OwletsFox
     *
     * Description:
     *   - Boundary case: operation exists but item list is empty.
     *   - Expected behavior:
     *       1) use case returns CartReport with empty items,
     *       2) still maps cart id and operation type.
     *   - Goal: keep report shape stable even when no items are present.
     */
    test("when operation has no items - returns cart report with empty items") {
        runTest {
            val opReport = CartOperationReport(
                employeeID = "emp-10",
                operationType = 3,
                operationTime = "2025-01-01T03:00:00Z",
                items = listOf(CartItemReport(productId = 777, quantity = 7, price = 70))
            )
            val ops = listOf(30 to opReport)

            every { cartOpRepo.getCartOperationReportsWithIds() } returns flowOf(ops)
            every { cartItemRepo.getCartItemReportsByOperationId(30) } returns flowOf(emptyList())

            val result = useCase()

            val expected = listOf(
                CartReport(
                    cartId = CartIdReport(
                        employeeId = "emp-10",
                        operationTime = "2025-01-01T03:00:00Z"
                    ),
                    operationType = 3,
                    items = emptyList()
                )
            )

            result shouldBe expected

            verify(exactly = 1) { cartOpRepo.getCartOperationReportsWithIds() }
            verify(exactly = 1) { cartItemRepo.getCartItemReportsByOperationId(30) }
            confirmVerified(cartOpRepo, cartItemRepo)
        }
    }
})
