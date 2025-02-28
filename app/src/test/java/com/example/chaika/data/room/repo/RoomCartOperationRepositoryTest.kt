package com.example.chaika.data.room.repo

import com.example.chaika.data.room.dao.CartOperationDao
import com.example.chaika.data.room.entities.CartOperation
import com.example.chaika.data.room.mappers.toInt
import com.example.chaika.domain.models.OperationTypeDomain
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

/**
 * Техника тест-дизайна: #1 Классы эквивалентности
 *
 * Автор: Кулаков Никита
 *
 * Описание:
 *   - Тест для RoomCartOperationRepository: позитивный сценарий, когда DAO возвращает корректный список операций.
 */
class RoomCartOperationRepositoryTest {
    private val cartOperationDao = mock(CartOperationDao::class.java)
    private val repository = RoomCartOperationRepository(cartOperationDao)

    @Test
    fun testGetCartOperationReportsWithIds_positive() =
        runBlocking {
            // Arrange
            val operations =
                listOf(
                    CartOperation(
                        id = 10,
                        operationType = OperationTypeDomain.ADD.toInt(),
                        operationTime = "2025-02-26T10:00:00",
                        conductorId = 101,
                    ),
                    CartOperation(
                        id = 20,
                        operationType = OperationTypeDomain.SOLD_CASH.toInt(),
                        operationTime = "2025-02-26T11:00:00",
                        conductorId = 102,
                    ),
                )
            whenever(cartOperationDao.getAllOperations()).thenReturn(flowOf(operations))

            // Act: получаем первую эмиссию Flow
            val result = repository.getCartOperationReportsWithIds().first()

            // Assert
            assertEquals(2, result.size)
            val firstPair = result[0]
            assertEquals(10, firstPair.first)
            assertEquals("101", firstPair.second.employeeID)
        }

    /**
     * Техника тест-дизайна: #2 Граничные значения
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для RoomCartOperationRepository: сценарий пустого списка.
     *   - Граничные значения: если DAO возвращает пустой список, то первая эмиссия содержит пустой список.
     */
    @Test
    fun testGetCartOperationReportsWithIds_empty() =
        runBlocking {
            // Arrange
            whenever(cartOperationDao.getAllOperations()).thenReturn(flowOf(emptyList()))

            // Act
            val result = repository.getCartOperationReportsWithIds().first()

            // Assert
            assertEquals(0, result.size)
        }
}
