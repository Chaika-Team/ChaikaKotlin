package com.chaikasoft.app.domain.usecases.productInfoUseCases

import com.chaikasoft.app.data.local.ImageSubDir
import com.chaikasoft.app.data.local.LocalImageRepositoryInterface
import com.chaikasoft.app.data.room.repo.RoomProductInfoRepositoryInterface
import com.chaikasoft.app.domain.models.ProductInfoDomain
import com.chaikasoft.app.domain.usecases.SaveProductsLocallyUseCase
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class SaveProductsLocallyUseCaseTest : FunSpec({

    lateinit var productRepository: RoomProductInfoRepositoryInterface
    lateinit var imageRepository: LocalImageRepositoryInterface
    lateinit var useCase: SaveProductsLocallyUseCase

    val productOne = ProductInfoDomain(
        id = 1,
        name = "Tea",
        description = "Black tea",
        image = "https://example.test/tea.jpg",
        price = 100,
    )
    val productTwo = ProductInfoDomain(
        id = 2,
        name = "Coffee",
        description = "Arabica",
        image = "https://example.test/coffee.jpg",
        price = 200,
    )
    val savedImagePath = "files/products/Tea.jpg"

    beforeTest {
        productRepository = mockk()
        imageRepository = mockk()

        useCase = SaveProductsLocallyUseCase(
            productInfoRepository = productRepository,
            localImageRepository = imageRepository,
            ioDispatcher = UnconfinedTestDispatcher(),
        )
    }

    /**
     * Техника тест-дизайна: #3 Таблица решений
     *
     * Описание:
     *   - Вход: список продуктов, где одно изображение сохраняется локально, а второе возвращает null.
     *   - Ожидаемое поведение: успешное сохранение заменяет image на локальный путь, ошибка оставляет remote URL.
     *   - Цель: зафиксировать best-effort политику сохранения изображений без срыва синка продуктов.
     */
    test("when image saving results are mixed - inserts each product with expected image") {
        runTest {
            val products = listOf(productOne, productTwo)
            val productOneSaved = productOne.copy(image = savedImagePath)
            val expectedProducts = listOf(productOneSaved, productTwo)

            coEvery { productRepository.getAllProductsOnce() } returns emptyList()
            coEvery {
                imageRepository.saveImageFromUrl(
                    imageUrl = productOne.image,
                    fileName = "${productOne.name}.jpg",
                    subDir = ImageSubDir.PRODUCTS.folder,
                )
            } returns savedImagePath
            coEvery {
                imageRepository.saveImageFromUrl(
                    imageUrl = productTwo.image,
                    fileName = "${productTwo.name}.jpg",
                    subDir = ImageSubDir.PRODUCTS.folder,
                )
            } returns null
            coEvery { productRepository.upsertAll(expectedProducts) } returns Unit

            val result = useCase(products)

            result shouldBe expectedProducts
            coVerify(exactly = 1) {
                imageRepository.saveImageFromUrl(
                    imageUrl = productOne.image,
                    fileName = "${productOne.name}.jpg",
                    subDir = ImageSubDir.PRODUCTS.folder,
                )
            }
            coVerify(exactly = 1) {
                imageRepository.saveImageFromUrl(
                    imageUrl = productTwo.image,
                    fileName = "${productTwo.name}.jpg",
                    subDir = ImageSubDir.PRODUCTS.folder,
                )
            }
            coVerify(exactly = 1) { productRepository.getAllProductsOnce() }
            coVerify(exactly = 1) { productRepository.upsertAll(expectedProducts) }
            confirmVerified(productRepository, imageRepository)
        }
    }

    /**
     * Техника тест-дизайна: #2 Граничные значения
     *
     * Описание:
     *   - Вход: продукт с пустым или blank image.
     *   - Ожидаемое поведение: LocalImageRepository не вызывается, в БД уходит пустая строка image.
     *   - Цель: гарантировать, что отсутствующие изображения не создают локальные файлы.
     */
    test("when image is missing - does not call saveImageFromUrl and stores empty image") {
        runTest {
            val productWithoutImage = productOne.copy(image = "  ")
            val productForStorage = productWithoutImage.copy(image = "")
            coEvery { productRepository.getAllProductsOnce() } returns emptyList()
            coEvery { productRepository.upsertAll(listOf(productForStorage)) } returns Unit

            val result = useCase(listOf(productWithoutImage))

            result shouldBe listOf(productForStorage)
            coVerify(exactly = 0) { imageRepository.saveImageFromUrl(any(), any(), any()) }
            coVerify(exactly = 1) { productRepository.getAllProductsOnce() }
            coVerify(exactly = 1) { productRepository.upsertAll(listOf(productForStorage)) }
            confirmVerified(productRepository, imageRepository)
        }
    }

    test("when existing image is local path - refreshes image to avoid stale local cache") {
        runTest {
            val localProduct = productOne.copy(image = savedImagePath)
            val expectedProducts = listOf(localProduct)
            coEvery { productRepository.getAllProductsOnce() } returns listOf(localProduct)
            coEvery {
                imageRepository.saveImageFromUrl(
                    imageUrl = productOne.image,
                    fileName = "${productOne.name}.jpg",
                    subDir = ImageSubDir.PRODUCTS.folder,
                )
            } returns savedImagePath

            val result = useCase(listOf(productOne))

            result shouldBe expectedProducts
            coVerify(exactly = 1) {
                imageRepository.saveImageFromUrl(
                    imageUrl = productOne.image,
                    fileName = "${productOne.name}.jpg",
                    subDir = ImageSubDir.PRODUCTS.folder,
                )
            }
            coVerify(exactly = 1) { productRepository.getAllProductsOnce() }
            coVerify(exactly = 0) { productRepository.upsertAll(any()) }
            confirmVerified(productRepository, imageRepository)
        }
    }

    test("when existing image is same remote url - skips image download and upsert") {
        runTest {
            coEvery { productRepository.getAllProductsOnce() } returns listOf(productOne)

            val result = useCase(listOf(productOne))

            result shouldBe listOf(productOne)
            coVerify(exactly = 0) { imageRepository.saveImageFromUrl(any(), any(), any()) }
            coVerify(exactly = 1) { productRepository.getAllProductsOnce() }
            coVerify(exactly = 0) { productRepository.upsertAll(any()) }
            confirmVerified(productRepository, imageRepository)
        }
    }
})
