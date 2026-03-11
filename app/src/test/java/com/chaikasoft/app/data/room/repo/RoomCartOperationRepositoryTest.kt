package com.chaikasoft.app.data.room.repo

import com.chaikasoft.app.data.room.dao.CartOperationDao
import com.chaikasoft.app.data.room.entities.CartItem
import com.chaikasoft.app.data.room.entities.CartOperation
import com.chaikasoft.app.data.room.entities.Conductor
import com.chaikasoft.app.data.room.entities.ProductInfo
import com.chaikasoft.app.data.room.relations.CartItemWithProduct
import com.chaikasoft.app.data.room.relations.CartOperationWithConductor
import com.chaikasoft.app.domain.models.OperationTypeDomain
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest

class RoomCartOperationRepositoryTest : FunSpec({

    lateinit var dao: CartOperationDao
    lateinit var repository: RoomCartOperationRepository

    beforeTest {
        dao = mockk()
        repository = RoomCartOperationRepository(dao)
    }

    /**
     * Test design: decision table.
     * countByType must delegate exact enum->int conversion (ordinal).
     */
    test("countByType delegates operation type ordinal into dao") {
        runTest {
            coEvery { dao.countByType(OperationTypeDomain.SOLD_CART.ordinal) } returns 7

            val count = repository.countByType(OperationTypeDomain.SOLD_CART)

            count shouldBe 7
            coVerify(exactly = 1) { dao.countByType(OperationTypeDomain.SOLD_CART.ordinal) }
        }
    }

    /**
     * Test design: equivalence classes.
     * Report flow should map relation rows into (id, reportHeader) pairs.
     */
    test("getCartOperationReportsWithIds maps relations to report pairs") {
        runTest {
            every { dao.getOperationsWithConductorForReport() } returns flowOf(
                listOf(
                    CartOperationWithConductor(
                        operation = CartOperation(
                            id = 11,
                            operationType = OperationTypeDomain.ADD.ordinal,
                            operationTime = "2026-03-09T10:00:00Z",
                            conductorId = 3,
                        ),
                        conductor = Conductor(
                            id = 3,
                            name = "Ivan",
                            familyName = "Petrov",
                            givenName = "Ivanovich",
                            employeeID = "EMP-3",
                            image = "img",
                        ),
                    ),
                ),
            )

            val pairs = repository.getCartOperationReportsWithIds().single()

            pairs.size shouldBe 1
            pairs.first().first shouldBe 11
            pairs.first().second.employeeID shouldBe "EMP-3"
            pairs.first().second.operationType shouldBe OperationTypeDomain.ADD.ordinal
        }
    }

    /**
     * Test design: equivalence classes.
     * observeOperationItems should map relation rows to cart domain with impacts.
     */
    test("observeOperationItems maps cart item relations to cart domain") {
        runTest {
            every { dao.observeItemsWithProducts(55) } returns flowOf(
                listOf(
                    CartItemWithProduct(
                        item = CartItem(id = 1, cartOperationId = 55, productId = 100, impact = 4),
                        product = ProductInfo(100, "Tea", "Black", "img", 150),
                    ),
                ),
            )

            val cart = repository.observeOperationItems(55).single()

            cart.items.size shouldBe 1
            cart.items.first().product.id shouldBe 100
            cart.items.first().quantity shouldBe 4
        }
    }
})

