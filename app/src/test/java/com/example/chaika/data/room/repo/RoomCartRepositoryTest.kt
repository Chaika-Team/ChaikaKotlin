package com.example.chaika.data.room.repo

import com.example.chaika.data.room.dao.CartItemDao
import com.example.chaika.data.room.dao.CartOperationDao
import com.example.chaika.data.room.entities.CartOperation
import com.example.chaika.domain.models.CartDomain
import com.example.chaika.domain.models.CartItemDomain
import com.example.chaika.domain.models.CartOperationDomain
import com.example.chaika.domain.models.OperationTypeDomain
import com.example.chaika.domain.models.ProductInfoDomain
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.kotlin.any // из библиотеки mockito-kotlin
import org.mockito.kotlin.whenever

class RoomCartRepositoryTest {
    private val cartItemDao = mock(CartItemDao::class.java)
    private val cartOperationDao = mock(CartOperationDao::class.java)
    private val repository = RoomCartRepository(cartItemDao, cartOperationDao)

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для RoomCartRepository: позитивный сценарий, когда корзина содержит элементы, и операция успешно сохраняется.
     */
    @Test
    fun testSaveCartWithItemsAndOperation_positive(): Unit =
        runBlocking {
            // Arrange
            val cartOperationDomain =
                CartOperationDomain(
                    operationTypeDomain = OperationTypeDomain.ADD,
                    conductorId = 50,
                )
            val cartItemsDomain =
                mutableListOf(
                    CartItemDomain(
                        product = ProductInfoDomain(1, "Prod1", "Desc1", "img1.png", 10.0),
                        quantity = 5,
                    ),
                    CartItemDomain(
                        product = ProductInfoDomain(2, "Prod2", "Desc2", "img2.png", 20.0),
                        quantity = 3,
                    ),
                )
            val cart = CartDomain(items = cartItemsDomain)
            // Здесь используем явный тип для аргумент-матчера: any<CartOperation>()
            whenever(cartOperationDao.insertOperation(any<CartOperation>())).thenReturn(100L)
            // Для insertCartItem – any<CartItem>()
            whenever(cartItemDao.insertCartItem(any())).thenReturn(1L)

            // Act
            repository.saveCartWithItemsAndOperation(cart, cartOperationDomain)

            // Assert
            verify(cartOperationDao, times(1)).insertOperation(any())
            verify(cartItemDao, times(cart.items.size)).insertCartItem(any())
        }

    /**
     * Техника тест-дизайна: #2 Граничные значения
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для RoomCartRepository: сценарий, когда корзина пуста.
     *   - Граничные значения: если список элементов пуст, insertCartItem не вызывается.
     */
    @Test
    fun testSaveCartWithItemsAndOperation_emptyCart(): Unit =
        runBlocking {
            // Arrange
            val cartOperationDomain =
                CartOperationDomain(
                    operationTypeDomain = OperationTypeDomain.ADD,
                    conductorId = 60,
                )
            val cart = CartDomain(items = mutableListOf())
            whenever(cartOperationDao.insertOperation(any<CartOperation>())).thenReturn(200L)

            // Act
            repository.saveCartWithItemsAndOperation(cart, cartOperationDomain)

            // Assert
            verify(cartOperationDao, times(1)).insertOperation(any())
            verify(cartItemDao, never()).insertCartItem(any())
        }

    /**
     * Техника тест-дизайна: #4 Прогнозирование ошибок
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для RoomCartRepository: негативный сценарий, когда insertOperation выбрасывает исключение.
     *   - Ожидается, что исключение пробрасывается.
     */
    @Test
    fun testSaveCartWithItemsAndOperation_exceptionOnInsertOperation(): Unit =
        runBlocking {
            // Arrange
            val cartOperationDomain =
                CartOperationDomain(
                    operationTypeDomain = OperationTypeDomain.ADD,
                    conductorId = 70,
                )
            val cart =
                CartDomain(
                    items =
                        mutableListOf(
                            CartItemDomain(
                                product = ProductInfoDomain(1, "Prod1", "Desc1", "img1.png", 10.0),
                                quantity = 5,
                            ),
                        ),
                )
            whenever(cartOperationDao.insertOperation(any<CartOperation>())).thenThrow(
                RuntimeException(
                    "DB insert failed",
                ),
            )

            // Act & Assert
            assertThrows(RuntimeException::class.java) {
                runBlocking {
                    repository.saveCartWithItemsAndOperation(cart, cartOperationDomain)
                }
            }
        }
}
