package com.chaikasoft.app.data.room.repo

import com.chaikasoft.app.data.room.dao.CartItemDao
import com.chaikasoft.app.data.room.dao.ProductInfoDao
import com.chaikasoft.app.data.room.entities.CartItem
import com.chaikasoft.app.data.room.entities.ProductInfo
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest

class RoomCartItemRepositoryTest : FunSpec({

    lateinit var cartItemDao: CartItemDao
    lateinit var productInfoDao: ProductInfoDao
    lateinit var repository: RoomCartItemRepository

    beforeTest {
        cartItemDao = mockk()
        productInfoDao = mockk()
        repository = RoomCartItemRepository(cartItemDao, productInfoDao)
    }

    /**
     * Test design: equivalence classes.
     * Repository should map cart items to report rows with product prices.
     */
    test("getCartItemReportsByOperationId maps cart items to report list") {
        runTest {
            every { cartItemDao.getCartItemsByCartOpId(10) } returns flowOf(
                listOf(
                    CartItem(id = 1, cartOperationId = 10, productId = 100, impact = 2),
                    CartItem(id = 2, cartOperationId = 10, productId = 101, impact = -1),
                ),
            )
            coEvery { productInfoDao.getProductById(100) } returns ProductInfo(100, "Tea", "Black", "img", 150)
            coEvery { productInfoDao.getProductById(101) } returns ProductInfo(101, "Coffee", "Arabica", "img2", 200)

            val reports = repository.getCartItemReportsByOperationId(10).single()

            reports.size shouldBe 2
            reports[0].productId shouldBe 100
            reports[0].quantity shouldBe 2
            reports[0].price shouldBe 150
            reports[1].productId shouldBe 101
            reports[1].quantity shouldBe -1
            reports[1].price shouldBe 200
        }
    }

    /**
     * Test design: error guessing.
     * Missing product metadata is invalid DB state and should throw.
     */
    test("getCartItemReportsByOperationId throws when product info is missing") {
        runTest {
            every { cartItemDao.getCartItemsByCartOpId(10) } returns flowOf(
                listOf(CartItem(id = 1, cartOperationId = 10, productId = 404, impact = 1)),
            )
            coEvery { productInfoDao.getProductById(404) } returns null

            shouldThrow<IllegalArgumentException> {
                repository.getCartItemReportsByOperationId(10).single()
            }
        }
    }
})

