package com.example.chaika.data.room.entities

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

/**
 * Тесты для сущности PackageItemView.
 */
class PackageItemViewTest {
    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: John Doe
     *
     * Описание:
     *   - Тест для создания PackageItemView с типичными значениями.
     *   - Классы эквивалентности: корректные данные для productId и currentQuantity.
     */
    @Test
    fun testPackageItemViewCreation() {
        val packageItemView =
            PackageItemView(
                productId = 5,
                currentQuantity = 12,
            )
        assertEquals(5, packageItemView.productId)
        assertEquals(12, packageItemView.currentQuantity)
    }

    /**
     * Техника тест-дизайна: #2 Граничные значения
     *
     * Автор: John Doe
     *
     * Описание:
     *   - Тест для проверки граничных значений currentQuantity.
     *   - Граничные значения: проверка currentQuantity = 0 и отрицательных значений (если применимо).
     */
    @Test
    fun testPackageItemViewBoundaries() {
        val zeroQuantity = PackageItemView(productId = 5, currentQuantity = 0)
        val negativeQuantity = PackageItemView(productId = 5, currentQuantity = -3)
        assertEquals(0, zeroQuantity.currentQuantity)
        assertEquals(-3, negativeQuantity.currentQuantity)
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: John Doe
     *
     * Описание:
     *   - Тест для проверки equals() и hashCode() для PackageItemView.
     */
    @Test
    fun testPackageItemViewEquality() {
        val view1 = PackageItemView(productId = 10, currentQuantity = 25)
        val view2 = PackageItemView(productId = 10, currentQuantity = 25)
        val viewDifferent = PackageItemView(productId = 10, currentQuantity = 30)
        assertEquals(view1, view2)
        assertEquals(view1.hashCode(), view2.hashCode())
        assertNotEquals(view1, viewDifferent)
    }
}
