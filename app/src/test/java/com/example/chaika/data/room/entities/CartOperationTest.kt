package com.example.chaika.data.room.entities

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

/**
 * Тесты для сущности CartOperation.
 */
class CartOperationTest {
    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для создания объекта CartOperation с типичными значениями.
     *   - Классы эквивалентности: корректные данные для operationType, operationTime и conductorId.
     */
    @Test
    fun testCartOperationCreation() {
        val opTime = "2025-02-26T12:00:00"
        val cartOperation =
            CartOperation(
                operationType = 0, // Например, ADD
                operationTime = opTime,
                conductorId = 42,
            )
        // Проверяем, что id по умолчанию равно 0
        assertEquals(0, cartOperation.id)
        assertEquals(0, cartOperation.operationType)
        assertEquals(opTime, cartOperation.operationTime)
        assertEquals(42, cartOperation.conductorId)
    }

    /**
     * Техника тест-дизайна: #2 Граничные значения
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для проверки граничных значений поля operationType.
     *   - Граничные значения: проверка operationType с отрицательным значением, нулём и максимальным ожидаемым значением (например, 3).
     */
    @Test
    fun testOperationTypeBoundaries() {
        val opTime = "2025-02-26T00:00:00"

        // Проверяем отрицательное значение
        val opNegative = CartOperation(operationType = -1, operationTime = opTime, conductorId = 1)
        assertEquals(-1, opNegative.operationType)

        // Проверяем типичное значение
        val opValid = CartOperation(operationType = 1, operationTime = opTime, conductorId = 1)
        assertEquals(1, opValid.operationType)

        // Проверяем верхнюю границу (её нет, тут должен быть TODO)
        val opUpper = CartOperation(operationType = 10, operationTime = opTime, conductorId = 1)
        assertEquals(10, opUpper.operationType)
    }

    /**
     * Техника тест-дизайна: #7 Таблица принятия решений
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для проверки методов equals() и hashCode() у CartOperation.
     *   - Таблица принятия решений: объекты с одинаковыми параметрами должны быть равны, а если хотя бы одно поле отличается – не равны.
     */
    @Test
    fun testCartOperationEquality() {
        val opTime = "2025-02-26T12:00:00"
        val cartOp1 = CartOperation(operationType = 0, operationTime = opTime, conductorId = 42)
        val cartOp2 = CartOperation(operationType = 0, operationTime = opTime, conductorId = 42)
        val cartOpDifferent =
            CartOperation(operationType = 1, operationTime = opTime, conductorId = 42)

        // Поскольку data-классы автоматически генерируют equals и hashCode, объекты с одинаковыми параметрами должны быть равны.
        assertEquals(cartOp1, cartOp2)
        assertEquals(cartOp1.hashCode(), cartOp2.hashCode())
        // Если хотя бы одно поле отличается, объекты должны быть не равны.
        assertNotEquals(cartOp1, cartOpDifferent)
    }
}
