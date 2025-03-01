package com.example.chaika.data.room.entities

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

/**
 * Тесты для сущности CartItem.
 */
class CartItemTest {
    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для создания CartItem с типичными значениями.
     *   - Классы эквивалентности: корректные данные для cartOperationId, productId и impact.
     */
    @Test
    fun testCartItemCreation() {
        val cartItem =
            CartItem(
                cartOperationId = 10,
                productId = 5,
                impact = 3,
            )
        // Проверяем, что id установлено по умолчанию (0)
        assertEquals(0, cartItem.id)
        assertEquals(10, cartItem.cartOperationId)
        assertEquals(5, cartItem.productId)
        assertEquals(3, cartItem.impact)
    }

    /**
     * Техника тест-дизайна: #2 Граничные значения
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для проверки граничных значений поля impact.
     *   - Граничные значения: проверка impact = 0, отрицательное и положительное значение.
     */
    @Test
    fun testCartItemImpactBoundaries() {
        val zeroImpact = CartItem(cartOperationId = 1, productId = 2, impact = 0)
        val negativeImpact = CartItem(cartOperationId = 1, productId = 2, impact = -10)
        val positiveImpact = CartItem(cartOperationId = 1, productId = 2, impact = 20)
        assertEquals(0, zeroImpact.impact)
        assertEquals(-10, negativeImpact.impact)
        assertEquals(20, positiveImpact.impact)
    }

    /**
     * Техника тест-дизайна: #7 Таблица принятия решений
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для проверки метода equals (и hashCode) у CartItem.
     *   - Таблица принятия решений: сравнение объектов с одинаковыми и различными значениями.
     */
    @Test
    fun testCartItemEquality() {
        val cartItem1 = CartItem(cartOperationId = 10, productId = 5, impact = 3)
        val cartItem2 = CartItem(cartOperationId = 10, productId = 5, impact = 3)
        val cartItem3 = CartItem(cartOperationId = 11, productId = 5, impact = 3)
        // Поскольку id по умолчанию равны (0), объекты с одинаковыми остальными параметрами должны быть равны.
        assertEquals(cartItem1, cartItem2)
        // Если хотя бы одно из полей (например, cartOperationId) отличается, объекты не равны.
        assertNotEquals(cartItem1, cartItem3)
    }
}
