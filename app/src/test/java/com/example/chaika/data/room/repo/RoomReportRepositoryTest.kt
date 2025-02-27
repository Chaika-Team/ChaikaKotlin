package com.example.chaika.data.room.repo

import com.example.chaika.data.room.dao.FastReportViewDao
import com.example.chaika.data.room.entities.FastReportView
import com.example.chaika.domain.models.FastReportDomain
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

class RoomReportRepositoryTest {
    private val fastReportViewDao = mock(FastReportViewDao::class.java)
    private val repository = RoomReportRepository(fastReportViewDao)

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: John Doe
     *
     * Описание:
     *   - Тест для getFastReportData, проверяется корректное маппирование из FastReportView в FastReportDomain.
     */
    @Test
    fun testGetFastReportData_positive() =
        runBlocking {
            // Arrange
            val reportViews =
                listOf(
                    FastReportView(
                        productName = "ProductA",
                        productPrice = 150.0,
                        addedQuantity = 10,
                        replenishedQuantity = 5,
                        soldCashQuantity = 2,
                        soldCartQuantity = 1,
                        revenue = 300.0,
                    ),
                    FastReportView(
                        productName = "ProductB",
                        productPrice = 200.0,
                        addedQuantity = 8,
                        replenishedQuantity = 4,
                        soldCashQuantity = 3,
                        soldCartQuantity = 2,
                        revenue = 600.0,
                    ),
                )
            whenever(fastReportViewDao.getReportData()).thenReturn(flowOf(reportViews))

            // Act
            val result: List<FastReportDomain> = repository.getFastReportData().first()

            // Assert
            assertEquals(2, result.size)
            assertEquals("ProductA", result[0].productName)
            assertEquals(300.0, result[0].revenue)
        }

    /**
     * Техника тест-дизайна: #2 Граничные значения
     *
     * Автор: John Doe
     *
     * Описание:
     *   - Тест для getFastReportData, сценарий пустого списка.
     *   - Если DAO возвращает пустой список, то результат – пустой список.
     */
    @Test
    fun testGetFastReportData_empty() =
        runBlocking {
            // Arrange
            whenever(fastReportViewDao.getReportData()).thenReturn(flowOf(emptyList()))

            // Act
            val result: List<FastReportDomain> = repository.getFastReportData().first()

            // Assert
            assertEquals(0, result.size)
        }
}
