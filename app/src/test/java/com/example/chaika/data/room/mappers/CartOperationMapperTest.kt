package com.example.chaika.data.room.mappers

import com.example.chaika.data.room.entities.CartOperation
import com.example.chaika.domain.models.CartItemReport
import com.example.chaika.domain.models.CartOperationDomain
import com.example.chaika.domain.models.CartOperationReport
import com.example.chaika.domain.models.OperationTypeDomain
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.util.regex.Pattern

/**
 * Тесты для мапперов, связанных с операциями корзины.
 */
class CartOperationMapperTest {
    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для функции Int.toOperationType с валидным индексом.
     *   - Классы эквивалентности: корректные данные (0 для ADD, 1 для SOLD_CASH, и т.д.).
     */
    @Test
    fun testToOperationType_valid() {
        assertEquals(OperationTypeDomain.ADD, 0.toOperationType())
        assertEquals(OperationTypeDomain.SOLD_CASH, 1.toOperationType())
        assertEquals(OperationTypeDomain.SOLD_CART, 2.toOperationType())
        assertEquals(OperationTypeDomain.REPLENISH, 3.toOperationType())
    }

    /**
     * Техника тест-дизайна: #2 Граничные значения
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для функции Int.toOperationType с некорректным индексом.
     *   - Граничные значения: индекс, равный размеру перечисления, должен выбросить исключение.
     */
    @Test
    fun testToOperationType_invalid() {
        val invalidIndex = OperationTypeDomain.entries.size // за границей допустимого диапазона
        assertThrows(IllegalArgumentException::class.java) {
            invalidIndex.toOperationType()
        }
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для функции OperationTypeDomain.toInt.
     *   - Классы эквивалентности: проверка корректного преобразования каждого типа в ordinal.
     */
    @Test
    fun testOperationTypeToInt() {
        assertEquals(0, OperationTypeDomain.ADD.toInt())
        assertEquals(1, OperationTypeDomain.SOLD_CASH.toInt())
        assertEquals(2, OperationTypeDomain.SOLD_CART.toInt())
        assertEquals(3, OperationTypeDomain.REPLENISH.toInt())
    }

    /**
     * Техника тест-дизайна: #2 Граничные значения
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для функции getCurrentTime.
     *   - Граничные значения: проверка формата возвращаемой строки (ISO 8601: yyyy-MM-dd'T'HH:mm:ss).
     */
    @Test
    fun testGetCurrentTime() {
        val currentTime = getCurrentTime()
        val regex = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(currentTime)
        assertEquals(true, matcher.matches())
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для функции CartOperation.toDomain.
     *   - Классы эквивалентности: корректное преобразование полей operationType и conductorId.
     */
    @Test
    fun testCartOperationToDomain() {
        // Arrange
        val operation =
            CartOperation(
                id = 5,
                operationType = OperationTypeDomain.SOLD_CART.toInt(),
                operationTime = "2025-02-26T12:34:56",
                conductorId = 99,
            )
        // Act
        val domain: CartOperationDomain = operation.toDomain()
        // Assert
        assertEquals(OperationTypeDomain.SOLD_CART, domain.operationTypeDomain)
        assertEquals(99, domain.conductorId)
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для функции CartOperationDomain.toEntity.
     *   - Классы эквивалентности: корректное преобразование полей и установка id равным 0.
     */
    @Test
    fun testCartOperationDomainToEntity() {
        // Arrange
        val domain =
            CartOperationDomain(
                operationTypeDomain = OperationTypeDomain.REPLENISH,
                conductorId = 42,
            )
        // Act
        val entity: CartOperation = domain.toEntity()
        // Assert: id должен быть 0, operationType соответствует ordinal, время должно соответствовать формату ISO 8601
        assertEquals(0, entity.id)
        assertEquals(OperationTypeDomain.REPLENISH.toInt(), entity.operationType)
        assertEquals(42, entity.conductorId)
        val regex = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(entity.operationTime)
        assertEquals(true, matcher.matches())
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для функции CartOperation.toReport.
     *   - Классы эквивалентности: корректное преобразование полей в репорт-модель.
     */
    @Test
    fun testCartOperationToReport() {
        // Arrange
        val operation =
            CartOperation(
                id = 10,
                operationType = OperationTypeDomain.ADD.toInt(),
                operationTime = "2025-02-26T15:00:00",
                conductorId = 77,
            )
        val reportItems =
            listOf<CartItemReport>(
                CartItemReport(productID = 1, quantity = 5, price = 100.0),
                CartItemReport(productID = 2, quantity = 3, price = 50.0),
            )
        // Act
        val report: CartOperationReport = operation.toReport(reportItems)
        // Assert: проверка полей репорт-модели
        assertEquals("77", report.employeeID)
        assertEquals(OperationTypeDomain.ADD.toInt(), report.operationType)
        assertEquals("2025-02-26T15:00:00", report.operationTime)
        assertEquals(reportItems, report.items)
    }
}
