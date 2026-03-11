package com.chaikasoft.app.data.room.repo

import com.chaikasoft.app.data.room.dao.PackageItemViewDao
import com.chaikasoft.app.data.room.dao.ProductInfoDao
import com.chaikasoft.app.data.room.entities.PackageItemView
import com.chaikasoft.app.data.room.entities.ProductInfo
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest

class RoomPackageItemRepositoryTest : FunSpec({

    lateinit var packageItemViewDao: PackageItemViewDao
    lateinit var productInfoDao: ProductInfoDao
    lateinit var repository: RoomPackageItemRepository

    beforeTest {
        packageItemViewDao = mockk()
        productInfoDao = mockk()
        repository = RoomPackageItemRepository(packageItemViewDao, productInfoDao)
    }

    /**
     * Test design: decision table.
     * mapNotNull branch: records without productInfo must be skipped.
     */
    test("getAllPackageItems skips rows without product info") {
        runTest {
            every { packageItemViewDao.getPackageItems() } returns flowOf(
                listOf(
                    PackageItemView(productId = 1, currentQuantity = 5),
                    PackageItemView(productId = 2, currentQuantity = 9),
                ),
            )
            coEvery { productInfoDao.getProductById(1) } returns ProductInfo(1, "Tea", "Black", "img", 150)
            coEvery { productInfoDao.getProductById(2) } returns null

            val items = repository.getAllPackageItems().single()

            items.size shouldBe 1
            items.first().productInfoDomain.id shouldBe 1
            items.first().currentQuantity shouldBe 5
        }
    }

    /**
     * Test design: boundary values.
     * Current quantity should fallback to 0 when view row is absent.
     */
    test("getCurrentQuantity returns zero when product is absent") {
        runTest {
            coEvery { packageItemViewDao.getPackageItemByProductId(123) } returns null

            repository.getCurrentQuantity(123) shouldBe 0
        }
    }
})

