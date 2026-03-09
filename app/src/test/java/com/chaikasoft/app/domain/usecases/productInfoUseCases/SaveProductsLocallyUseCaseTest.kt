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
import kotlinx.coroutines.test.runTest

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
        useCase = SaveProductsLocallyUseCase(productRepository, imageRepository)
    }

    /**
     * Техника тест-дизайна: #3 Таблица решений
     *
     * Автор: Codex
     *
     * Описание:
     *   - Входы: смешанные результаты сохранения изображения (путь vs null).
     *   - Ожидаемое поведение:
     *       1) путь сохраненного изображения заменяет image перед вставкой,
     *       2) null путь сохраняет исходный image,
     *       3) каждый товар вставляется,
     *       4) возвращается исходный список.
     *   - Цель: зафиксировать поведение по каждому товару и контракт возврата.
     */
    test("when image saving results are mixed - inserts each product with expected image and returns original list") {
        runTest {
            val products = listOf(productOne, productTwo)
            val inserted = mutableListOf<ProductInfoDomain>()
            val productOneSaved = productOne.copy(image = savedImagePath)

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
            coEvery { productRepository.insertProduct(capture(inserted)) } returns Unit

            val result = useCase(products)

            result shouldBe products
            inserted.toSet() shouldBe setOf(productOneSaved, productTwo)
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
            coVerify(exactly = 2) { productRepository.insertProduct(any()) }
            confirmVerified(productRepository, imageRepository)
        }
    }
})
