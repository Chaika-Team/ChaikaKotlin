@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.example.chaika.domain.usecases

import com.example.chaika.data.dataSource.ProductInfoDataSourceInterface
import com.example.chaika.data.local.ImageSubDir
import com.example.chaika.data.local.LocalImageRepositoryInterface
import com.example.chaika.data.room.repo.RoomProductInfoRepositoryInterface
import com.example.chaika.domain.models.ProductInfoDomain
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class ProductInfoUseCasesTest {

    @Mock
    lateinit var roomProductInfoRepository: RoomProductInfoRepositoryInterface

    @Mock
    lateinit var productInfoDataSource: ProductInfoDataSourceInterface

    @Mock
    lateinit var localImageRepository: LocalImageRepositoryInterface

    // Dummy объекты для тестов:
    private val dummyProduct1 = ProductInfoDomain(
        id = 1,
        name = "Product 1",
        description = "Desc 1",
        image = "img1",
        price = 10.0
    )
    private val dummyProduct2 = ProductInfoDomain(
        id = 2,
        name = "Product 2",
        description = "Desc 2",
        image = "img2",
        price = 15.0
    )

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: OwletsFox
     *
     * Описание:
     *  - Тест для GetAllProductsUseCase.
     *  - Проверяется, что use case возвращает Flow со списком товаров, полученным из roomProductInfoRepository.
     */
    @Test
    fun `GetAllProductsUseCase returns correct flow data`() = runTest {
        val expectedList = listOf(dummyProduct1, dummyProduct2)
        whenever(roomProductInfoRepository.getAllProducts()).thenReturn(flowOf(expectedList))
        val useCase = GetAllProductsUseCase(roomProductInfoRepository)
        val actualList = useCase.invoke().first()
        assertEquals(expectedList, actualList)
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: OwletsFox
     *
     * Описание:
     *  - Тест для AddProductUseCase.
     *  - Проверяется, что use case вызывает метод insertProduct() у roomProductInfoRepository с переданным товаром.
     */
    @Test
    fun `AddProductUseCase calls insertProduct`() = runTest {
        val useCase = AddProductUseCase(roomProductInfoRepository)
        useCase.invoke(dummyProduct1)
        verify(roomProductInfoRepository).insertProduct(dummyProduct1)
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: OwletsFox
     *
     * Описание:
     *  - Тест для DeleteProductUseCase.
     *  - Проверяется, что use case вызывает метод deleteProduct() у roomProductInfoRepository с переданным товаром.
     */
    @Test
    fun `DeleteProductUseCase calls deleteProduct`() = runTest {
        val useCase = DeleteProductUseCase(roomProductInfoRepository)
        useCase.invoke(dummyProduct2)
        verify(roomProductInfoRepository).deleteProduct(dummyProduct2)
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: OwletsFox
     *
     * Описание:
     *  - Тест для AddProductInfoUseCase.
     *  - Проверяется, что use case получает список товаров с productInfoDataSource,
     *    сохраняет изображение для каждого товара через localImageRepository,
     *    заменяет поле image, если сохранение прошло успешно, и вызывает insertProduct() для каждого товара.
     */
    @Test
    fun `AddProductInfoUseCase saves products with updated image path when image is saved successfully`() =
        runTest {
            val productList = listOf(dummyProduct1, dummyProduct2)
            whenever(productInfoDataSource.fetchProductInfoList()).thenReturn(productList)
            whenever(
                localImageRepository.saveImageFromUrl(
                    imageUrl = dummyProduct1.image,
                    fileName = "${dummyProduct1.name}.jpg",
                    subDir = ImageSubDir.PRODUCTS.folder
                )
            ).thenReturn("new_img1")
            whenever(
                localImageRepository.saveImageFromUrl(
                    imageUrl = dummyProduct2.image,
                    fileName = "${dummyProduct2.name}.jpg",
                    subDir = ImageSubDir.PRODUCTS.folder
                )
            ).thenReturn(null)
            val useCase = AddProductInfoUseCase(
                roomProductInfoRepository,
                productInfoDataSource,
                localImageRepository
            )
            useCase.invoke()
            val expectedProduct1 = dummyProduct1.copy(image = "new_img1")
            val expectedProduct2 = dummyProduct2
            verify(roomProductInfoRepository).insertProduct(expectedProduct1)
            verify(roomProductInfoRepository).insertProduct(expectedProduct2)
        }
}
