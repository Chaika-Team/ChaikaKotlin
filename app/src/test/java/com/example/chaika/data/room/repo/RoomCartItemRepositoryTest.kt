package com.example.chaika.data.room.repo

import com.example.chaika.data.room.dao.CartItemDao
import com.example.chaika.data.room.dao.ProductInfoDao
import com.example.chaika.data.room.entities.CartItem
import com.example.chaika.data.room.entities.ProductInfo
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

/**
 * Техника тест-дизайна: #1 Классы эквивалентности
 *
 * Автор: Кулаков Никита
 *
 * Описание:
 *   - Тест для RoomCartItemRepository: позитивный сценарий, когда DAO возвращает корректный список CartItem и productInfoDao возвращает ProductInfo.
 */
class RoomCartItemRepositoryTest {
    private val cartItemDao = mock(CartItemDao::class.java)
    private val productInfoDao = mock(ProductInfoDao::class.java)
    private val repository = RoomCartItemRepository(cartItemDao, productInfoDao)

    @Test
    fun testGetCartItemReportsByOperationId_positive() =
        runBlocking {
            // Arrange
            val operationId = 100
            val cartItems =
                listOf(
                    CartItem(cartOperationId = operationId, productId = 1, impact = 5),
                    CartItem(cartOperationId = operationId, productId = 2, impact = 3),
                )
            val product1 =
                ProductInfo(
                    id = 1,
                    name = "Prod1",
                    description = "Desc1",
                    image = "img1.png",
                    price = 10.0,
                )
            val product2 =
                ProductInfo(
                    id = 2,
                    name = "Prod2",
                    description = "Desc2",
                    image = "img2.png",
                    price = 20.0,
                )
            whenever(cartItemDao.getCartItemsByCartOpId(operationId)).thenReturn(flowOf(cartItems))
            whenever(productInfoDao.getProductById(1)).thenReturn(product1)
            whenever(productInfoDao.getProductById(2)).thenReturn(product2)

            // Act: получаем первую эмиссию Flow
            val result = repository.getCartItemReportsByOperationId(operationId).first()

            // Assert
            assertEquals(2, result.size)
            assertEquals(1, result[0].productID)
            assertEquals(5, result[0].quantity)
            assertEquals(10.0, result[0].price)
            assertEquals(2, result[1].productID)
            assertEquals(3, result[1].quantity)
            assertEquals(20.0, result[1].price)
        }

    /**
     * Техника тест-дизайна: #4 Прогнозирование ошибок
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для RoomCartItemRepository: негативный сценарий, когда productInfoDao возвращает null.
     *   - Ожидается выброс IllegalArgumentException.
     */
    @Test
    fun testGetCartItemReportsByOperationId_productNotFound(): Unit =
        runBlocking {
            // Arrange
            val operationId = 200
            val cartItems =
                listOf(
                    CartItem(cartOperationId = operationId, productId = 99, impact = 7),
                )
            whenever(cartItemDao.getCartItemsByCartOpId(operationId)).thenReturn(flowOf(cartItems))
            whenever(productInfoDao.getProductById(99)).thenReturn(null)

            // Act & Assert: пробрасывается исключение
            assertThrows(IllegalArgumentException::class.java) {
                runBlocking { repository.getCartItemReportsByOperationId(operationId).first() }
            }
        }

    /**
     * Техника тест-дизайна: #2 Граничные значения
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для RoomCartItemRepository: сценарий пустого списка.
     *   - Граничные значения: если DAO возвращает пустой список, то внутри эмиссии содержится пустой список.
     */
    @Test
    fun testGetCartItemReportsByOperationId_emptyList() =
        runBlocking {
            // Arrange
            val operationId = 300
            whenever(cartItemDao.getCartItemsByCartOpId(operationId)).thenReturn(flowOf(emptyList()))

            // Act
            val result = repository.getCartItemReportsByOperationId(operationId).first()

            // Assert
            assertEquals(0, result.size)
        }
}
