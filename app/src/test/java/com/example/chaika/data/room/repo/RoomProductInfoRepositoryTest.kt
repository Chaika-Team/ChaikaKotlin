package com.example.chaika.data.room.repo

import com.example.chaika.data.room.dao.ProductInfoDao
import com.example.chaika.data.room.entities.ProductInfo
import com.example.chaika.domain.models.ProductInfoDomain
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.whenever

class RoomProductInfoRepositoryTest {
    private val productInfoDao = mock(ProductInfoDao::class.java)
    private val repository = RoomProductInfoRepository(productInfoDao)

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для getAllProducts, проверяется корректное маппирование сущностей в ProductInfoDomain.
     */
    @Test
    fun testGetAllProducts_positive() =
        runBlocking {
            // Arrange
            val products =
                listOf(
                    ProductInfo(
                        id = 1,
                        name = "Prod1",
                        description = "Desc1",
                        image = "img1.png",
                        price = 50.0,
                    ),
                    ProductInfo(
                        id = 2,
                        name = "Prod2",
                        description = "Desc2",
                        image = "img2.png",
                        price = 75.0,
                    ),
                )
            whenever(productInfoDao.getAllProducts()).thenReturn(flowOf(products))

            // Act
            val result: List<ProductInfoDomain> = repository.getAllProducts().first()

            // Assert
            assertEquals(2, result.size)
            assertEquals("Prod1", result[0].name)
            assertEquals(75.0, result[1].price)
        }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для insertProduct, updateProduct и deleteProduct.
     *   - Проверяется, что соответствующие методы DAO вызываются с преобразованными объектами.
     */
    @Test
    fun testProductInfoDaoCallsUsingArgumentCaptor() =
        runBlocking {
            // Arrange: создаём тестовую доменную модель продукта
            val productDomain = ProductInfoDomain(3, "Prod3", "Desc3", "img3.png", 100.0)

            // Act: вызываем методы репозитория
            repository.insertProduct(productDomain)
            repository.updateProduct(productDomain)
            repository.deleteProduct(productDomain)

            // Assert: используем аргумент-капторы для проверки объектов, переданных в DAO
            val insertCaptor = argumentCaptor<ProductInfo>()
            verify(productInfoDao, times(1)).insertProduct(insertCaptor.capture())
            val capturedInsert = insertCaptor.firstValue
            assertEquals(productDomain.id, capturedInsert.id)
            assertEquals(productDomain.name, capturedInsert.name)
            assertEquals(productDomain.description, capturedInsert.description)
            assertEquals(productDomain.image, capturedInsert.image)
            assertEquals(productDomain.price, capturedInsert.price)

            val updateCaptor = argumentCaptor<ProductInfo>()
            verify(productInfoDao, times(1)).updateProduct(updateCaptor.capture())
            val capturedUpdate = updateCaptor.firstValue
            assertEquals(productDomain.id, capturedUpdate.id)
            assertEquals(productDomain.name, capturedUpdate.name)
            assertEquals(productDomain.description, capturedUpdate.description)
            assertEquals(productDomain.image, capturedUpdate.image)
            assertEquals(productDomain.price, capturedUpdate.price)

            val deleteCaptor = argumentCaptor<ProductInfo>()
            verify(productInfoDao, times(1)).deleteProduct(deleteCaptor.capture())
            val capturedDelete = deleteCaptor.firstValue
            assertEquals(productDomain.id, capturedDelete.id)
            assertEquals(productDomain.name, capturedDelete.name)
            assertEquals(productDomain.description, capturedDelete.description)
            assertEquals(productDomain.image, capturedDelete.image)
            assertEquals(productDomain.price, capturedDelete.price)
        }
}
