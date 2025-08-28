package com.example.chaika.data.room.mappers

import com.example.chaika.data.room.entities.OperationInfoView
import com.example.chaika.domain.models.OperationTypeDomain
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

/**
 * Тесты для маппера OperationInfoView -> OperationSummaryDomain.
 */
class OperationSummaryMapperTest {

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: OwletsFox
     *
     * Описание:
     *   - Корректное преобразование OperationInfoView в OperationSummaryDomain.
     *   - Проверяются тип, время, данные проводника, количество строк товаров и общая цена.
     */
    @Test
    fun testOperationInfoView_toDomain_positive() {
        // Arrange
        val view = OperationInfoView(
            operationId = 100,
            operationType = OperationTypeDomain.SOLD_CASH.ordinal,
            operationTime = "2025-08-01T10:00:00",
            conductorId = 7,
            conductorName = "Alice",
            conductorFamilyName = "Ivanova",
            conductorGivenName = "A.",
            productLineQuantity = 2,
            totalPrice = 7.5
        )

        // Act
        val domain = view.toDomain()

        // Assert
        assertEquals(100, domain.id)
        assertEquals(OperationTypeDomain.SOLD_CASH, domain.type)
        assertEquals("2025-08-01T10:00:00", domain.timeIso)

        assertEquals(7, domain.conductor.id)
        assertEquals("Alice", domain.conductor.name)
        assertEquals("Ivanova", domain.conductor.familyName)
        assertEquals("A.", domain.conductor.givenName)

        assertEquals(2, domain.productLineQuantity)
        assertEquals(7.5, domain.totalPrice)
    }

    /**
     * Техника тест-дизайна: Прогнозирование ошибок
     *
     * Автор: OwletsFox
     *
     * Описание:
     *   - Некорректный индекс типа операции приводит к IllegalArgumentException
     *     внутри расширения Int.toOperationType().
     */
    @Test
    fun testOperationInfoView_toDomain_invalidType() {
        // Arrange: используем индекс за пределами перечисления
        val badType = OperationTypeDomain.entries.size
        val view = OperationInfoView(
            operationId = 1,
            operationType = badType,
            operationTime = "2025-08-02T00:00:00",
            conductorId = 1,
            conductorName = "X",
            conductorFamilyName = "Y",
            conductorGivenName = "Z",
            productLineQuantity = 0,
            totalPrice = 0.0
        )

        // Act + Assert
        assertThrows(IllegalArgumentException::class.java) {
            view.toDomain()
        }
    }
}
