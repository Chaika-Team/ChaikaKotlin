package com.chaikasoft.app.domain.usecases.productInfoUseCases

import com.chaikasoft.app.data.room.repo.RoomProductInfoRepositoryInterface
import com.chaikasoft.app.domain.models.ProductInfoDomain
import com.chaikasoft.app.domain.usecases.GetProductsByIdsUseCase
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest

class GetProductsByIdsUseCaseTest : FunSpec({

    lateinit var repository: RoomProductInfoRepositoryInterface
    lateinit var useCase: GetProductsByIdsUseCase

    val productTea = ProductInfoDomain(
        id = 10,
        name = "Tea",
        description = "Black tea",
        image = "tea.png",
        price = 120
    )

    beforeTest {
        repository = mockk()
        useCase = GetProductsByIdsUseCase(repository)
    }

    test("returns products mapped by id") {
        runTest {
            coEvery { repository.getProductsByIds(listOf(10, 20)) } returns listOf(productTea)

            val result = useCase(listOf(10, 20))

            result shouldBe mapOf(10 to productTea)
            coVerify(exactly = 1) { repository.getProductsByIds(listOf(10, 20)) }
        }
    }

    test("returns empty map without repository call when ids are empty") {
        runTest {
            val result = useCase(emptyList())

            result shouldBe emptyMap()
            coVerify(exactly = 0) { repository.getProductsByIds(any()) }
        }
    }
})
