package com.chaikasoft.app.data.room.repo

import com.chaikasoft.app.data.room.dao.ProductInfoDao
import com.chaikasoft.app.data.room.entities.ProductInfo
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest

class RoomProductInfoRepositoryTest : FunSpec({

    lateinit var productInfoDao: ProductInfoDao
    lateinit var repository: RoomProductInfoRepository

    beforeTest {
        productInfoDao = mockk()
        repository = RoomProductInfoRepository(productInfoDao)
    }

    test("getProductsByIds returns mapped products from dao") {
        runTest {
            coEvery { productInfoDao.getProductsByIds(listOf(1, 2)) } returns listOf(
                ProductInfo(1, "Tea", "Black", "tea.png", 150)
            )

            val products = repository.getProductsByIds(listOf(1, 2))

            products.size shouldBe 1
            products.first().id shouldBe 1
            products.first().name shouldBe "Tea"
            coVerify(exactly = 1) { productInfoDao.getProductsByIds(listOf(1, 2)) }
        }
    }

    test("getProductsByIds returns empty list without dao call when ids are empty") {
        runTest {
            val products = repository.getProductsByIds(emptyList())

            products shouldBe emptyList()
            coVerify(exactly = 0) { productInfoDao.getProductsByIds(any()) }
        }
    }
})
