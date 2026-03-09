package com.chaikasoft.app.domain.usecases.productInfoUseCases

import com.chaikasoft.app.domain.models.ProductInfoDomain
import com.chaikasoft.app.domain.usecases.FetchAndSaveProductsUseCase
import com.chaikasoft.app.domain.usecases.FetchProductsFromServerUseCase
import com.chaikasoft.app.domain.usecases.SaveProductsLocallyUseCase
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.test.runTest

class FetchAndSaveProductsUseCaseTest : FunSpec({

    lateinit var fetchProductsFromServerUseCase: FetchProductsFromServerUseCase
    lateinit var saveProductsLocallyUseCase: SaveProductsLocallyUseCase
    lateinit var useCase: FetchAndSaveProductsUseCase

    val products = listOf(
        ProductInfoDomain(
            id = 1,
            name = "Tea",
            description = "Black tea",
            image = "https://example.test/tea.jpg",
            price = 100,
        )
    )

    beforeTest {
        fetchProductsFromServerUseCase = mockk()
        saveProductsLocallyUseCase = mockk()
        useCase = FetchAndSaveProductsUseCase(
            fetchProductsFromServerUseCase,
            saveProductsLocallyUseCase,
        )
    }

    /**
     * Техника тест-дизайна: #3 Таблица решений
     *
     * Автор: Codex
     *
     * Описание:
     *   - Входы: fetch успешен и save успешен.
     *   - Ожидаемое поведение:
     *       1) полученные товары передаются в save,
     *       2) возвращается результат save.
     *   - Цель: стабилизировать контракт happy path.
     */
    test("when fetch succeeds - saves fetched products and returns save result") {
        runTest {
            val limit = 10
            val offset = 5
            val savedProducts = products.map { it.copy(image = "files/products/${it.name}.jpg") }

            coEvery { fetchProductsFromServerUseCase(limit, offset) } returns products
            coEvery { saveProductsLocallyUseCase(products) } returns savedProducts

            val result = useCase(limit, offset)

            result shouldBe savedProducts
            coVerify(exactly = 1) { fetchProductsFromServerUseCase(limit, offset) }
            coVerify(exactly = 1) { saveProductsLocallyUseCase(products) }
            confirmVerified(fetchProductsFromServerUseCase, saveProductsLocallyUseCase)
        }
    }

    /**
     * Техника тест-дизайна: #5 Error guessing
     *
     * Автор: Codex
     *
     * Описание:
     *   - Сценарий: fetch падает с исключением.
     *   - Ожидаемое поведение:
     *       1) исключение пробрасывается,
     *       2) save не вызывается.
     *   - Цель: избежать побочных эффектов при падении fetch.
     */
    test("when fetch fails - rethrows and skips save") {
        runTest {
            val limit = 10
            val offset = 5
            val error = IllegalStateException("boom")

            coEvery { fetchProductsFromServerUseCase(limit, offset) } throws error

            shouldThrow<IllegalStateException> { useCase(limit, offset) }

            coVerify(exactly = 1) { fetchProductsFromServerUseCase(limit, offset) }
            coVerify(exactly = 0) { saveProductsLocallyUseCase(any()) }
            confirmVerified(fetchProductsFromServerUseCase, saveProductsLocallyUseCase)
        }
    }

    /**
     * Техника тест-дизайна: #5 Error guessing
     *
     * Автор: Codex
     *
     * Описание:
     *   - Сценарий: fetch успешен, save падает с исключением.
     *   - Ожидаемое поведение: исключение пробрасывается после попытки save.
     *   - Цель: убедиться, что ошибки save не проглатываются.
     */
    test("when save fails - rethrows") {
        runTest {
            val limit = 10
            val offset = 5
            val error = IllegalStateException("boom")

            coEvery { fetchProductsFromServerUseCase(limit, offset) } returns products
            coEvery { saveProductsLocallyUseCase(products) } throws error

            shouldThrow<IllegalStateException> { useCase(limit, offset) }

            coVerify(exactly = 1) { fetchProductsFromServerUseCase(limit, offset) }
            coVerify(exactly = 1) { saveProductsLocallyUseCase(products) }
            confirmVerified(fetchProductsFromServerUseCase, saveProductsLocallyUseCase)
        }
    }
})
