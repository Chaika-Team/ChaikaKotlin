package com.example.chaika.data.room.repo

import com.example.chaika.data.room.dao.PackageItemViewDao
import com.example.chaika.data.room.dao.ProductInfoDao
import com.example.chaika.data.room.entities.PackageItemView
import com.example.chaika.data.room.entities.ProductInfo
import com.example.chaika.domain.models.PackageItemDomain
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

class RoomPackageItemRepositoryTest {
    private val packageItemViewDao = mock(PackageItemViewDao::class.java)
    private val productInfoDao = mock(ProductInfoDao::class.java)
    private val repository = RoomPackageItemRepository(packageItemViewDao, productInfoDao)

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для getAllPackageItems: позитивный сценарий, когда все элементы успешно маппятся.
     */
    @Test
    fun testGetAllPackageItems_positive() =
        runBlocking {
            // Arrange
            val packageItemViews =
                listOf(
                    PackageItemView(productId = 1, currentQuantity = 10),
                    PackageItemView(productId = 2, currentQuantity = 5),
                )
            val product1 =
                ProductInfo(
                    id = 1,
                    name = "Product1",
                    description = "Desc1",
                    image = "img1.png",
                    price = 100.0,
                )
            val product2 =
                ProductInfo(
                    id = 2,
                    name = "Product2",
                    description = "Desc2",
                    image = "img2.png",
                    price = 200.0,
                )
            whenever(packageItemViewDao.getPackageItems()).thenReturn(flowOf(packageItemViews))
            whenever(productInfoDao.getProductById(1)).thenReturn(product1)
            whenever(productInfoDao.getProductById(2)).thenReturn(product2)

            // Act
            val result: List<PackageItemDomain> = repository.getAllPackageItems().first()

            // Assert
            assertEquals(2, result.size)
            // Проверяем первое значение
            assertEquals(1, result[0].productInfoDomain.id)
            assertEquals(10, result[0].currentQuantity)
            // Проверяем второе значение
            assertEquals(2, result[1].productInfoDomain.id)
            assertEquals(5, result[1].currentQuantity)
        }

    /**
     * Техника тест-дизайна: #4 Прогнозирование ошибок
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для getAllPackageItems: негативный сценарий, когда для одного элемента productInfoDao возвращает null.
     *   - Такой элемент должен быть отфильтрован.
     */
    @Test
    fun testGetAllPackageItems_productNotFound() =
        runBlocking {
            // Arrange
            val packageItemViews =
                listOf(
                    PackageItemView(productId = 99, currentQuantity = 7),
                )
            whenever(packageItemViewDao.getPackageItems()).thenReturn(flowOf(packageItemViews))
            whenever(productInfoDao.getProductById(99)).thenReturn(null)

            // Act
            val result: List<PackageItemDomain> = repository.getAllPackageItems().first()

            // Assert
            assertEquals(0, result.size)
        }

    /**
     * Техника тест-дизайна: #2 Граничные значения
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для getPackageItemByProductId: если один из DAO возвращает null, результат должен быть null.
     */
    @Test
    fun testGetPackageItemByProductId_notFound() =
        runBlocking {
            // Arrange
            whenever(packageItemViewDao.getPackageItemByProductId(1)).thenReturn(null)
            whenever(productInfoDao.getProductById(1)).thenReturn(null)

            // Act
            val result = repository.getPackageItemByProductId(1)

            // Assert
            assertNull(result)
        }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для getPackageItemByProductId: позитивный сценарий, когда DAO возвращают корректные данные.
     */
    @Test
    fun testGetPackageItemByProductId_positive() =
        runBlocking {
            // Arrange
            val packageItemView = PackageItemView(productId = 2, currentQuantity = 15)
            val product2 =
                ProductInfo(
                    id = 2,
                    name = "Product2",
                    description = "Desc2",
                    image = "img2.png",
                    price = 250.0,
                )
            whenever(packageItemViewDao.getPackageItemByProductId(2)).thenReturn(packageItemView)
            whenever(productInfoDao.getProductById(2)).thenReturn(product2)

            // Act
            val result = repository.getPackageItemByProductId(2)

            // Assert
            assertNotNull(result)
            assertEquals(2, result?.productInfoDomain?.id)
            assertEquals(15, result?.currentQuantity)
        }
}
