package com.example.chaika.data.room.mappers

import com.example.chaika.data.room.entities.FastReportView
import com.example.chaika.domain.models.FastReportDomain
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Тесты для маппера быстрого отчёта.
 */
class FastReportMapperTest {
    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: John Doe
     *
     * Описание:
     *   - Тест для функции FastReportView.toDomain.
     *   - Классы эквивалентности: корректное преобразование всех полей из FastReportView в FastReportDomain.
     */
    @Test
    fun testFastReportViewToDomain() {
        // Arrange
        val fastReportView =
            FastReportView(
                productName = "Report Product",
                productPrice = 250.0,
                addedQuantity = 10,
                replenishedQuantity = 5,
                soldCashQuantity = 3,
                soldCartQuantity = 2,
                revenue = 750.0,
            )
        // Act
        val domain: FastReportDomain = fastReportView.toDomain()
        // Assert: проверяем соответствие всех полей
        assertEquals("Report Product", domain.productName)
        assertEquals(250.0, domain.productPrice)
        assertEquals(10, domain.addedQuantity)
        assertEquals(5, domain.replenishedQuantity)
        assertEquals(3, domain.soldCashQuantity)
        assertEquals(2, domain.soldCartQuantity)
        assertEquals(750.0, domain.revenue)
    }
}
