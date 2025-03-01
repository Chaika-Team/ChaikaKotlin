package com.example.chaika.data.room.entities

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

/**
 * Тесты для сущности FastReportView.
 */
class FastReportViewTest {
    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для FastReportView с типичными значениями.
     *   - Классы эквивалентности: корректные данные для productName, productPrice и агрегированных полей.
     */
    @Test
    fun testFastReportViewCreation_typical() {
        val reportView =
            FastReportView(
                productName = "Coffee",
                productPrice = 2.5,
                addedQuantity = 10,
                replenishedQuantity = 5,
                soldCashQuantity = 3,
                soldCartQuantity = 2,
                revenue = 7.5,
            )
        assertEquals("Coffee", reportView.productName)
        assertEquals(2.5, reportView.productPrice)
        assertEquals(10, reportView.addedQuantity)
        assertEquals(5, reportView.replenishedQuantity)
        assertEquals(3, reportView.soldCashQuantity)
        assertEquals(2, reportView.soldCartQuantity)
        assertEquals(7.5, reportView.revenue)
    }

    /**
     * Техника тест-дизайна: #2 Граничные значения
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для FastReportView с нулевыми значениями.
     *   - Граничные значения: все числовые поля равны 0.
     */
    @Test
    fun testFastReportViewCreation_allZero() {
        val reportView =
            FastReportView(
                productName = "Empty",
                productPrice = 0.0,
                addedQuantity = 0,
                replenishedQuantity = 0,
                soldCashQuantity = 0,
                soldCartQuantity = 0,
                revenue = 0.0,
            )
        assertEquals("Empty", reportView.productName)
        assertEquals(0.0, reportView.productPrice)
        assertEquals(0, reportView.addedQuantity)
        assertEquals(0, reportView.replenishedQuantity)
        assertEquals(0, reportView.soldCashQuantity)
        assertEquals(0, reportView.soldCartQuantity)
        assertEquals(0.0, reportView.revenue)
    }

    /**
     * Техника тест-дизайна: #7 Таблица принятия решений
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для сравнения объектов FastReportView.
     *   - Таблица принятия решений: объекты с одинаковыми параметрами должны быть равны, а при изменении хотя бы одного поля – не равны.
     */
    @Test
    fun testFastReportViewEquality() {
        val reportView1 =
            FastReportView(
                productName = "Tea",
                productPrice = 3.0,
                addedQuantity = 5,
                replenishedQuantity = 2,
                soldCashQuantity = 1,
                soldCartQuantity = 0,
                revenue = 3.0,
            )
        val reportView2 =
            FastReportView(
                productName = "Tea",
                productPrice = 3.0,
                addedQuantity = 5,
                replenishedQuantity = 2,
                soldCashQuantity = 1,
                soldCartQuantity = 0,
                revenue = 3.0,
            )
        val reportViewDifferent =
            FastReportView(
                productName = "Tea",
                productPrice = 3.0,
                addedQuantity = 5,
                replenishedQuantity = 2,
                soldCashQuantity = 2, // отличие
                soldCartQuantity = 0,
                revenue = 3.0,
            )
        assertEquals(reportView1, reportView2)
        assertEquals(reportView1.hashCode(), reportView2.hashCode())
        assertNotEquals(reportView1, reportViewDifferent)
    }

    /**
     * Техника тест-дизайна: #2 Граничные значения
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для FastReportView с экстремальными значениями.
     *   - Используются граничные значения: Double.MAX_VALUE, Int.MAX_VALUE и минимальные значения.
     */
    @Test
    fun testFastReportViewCreation_extremeValues() {
        val extremeView =
            FastReportView(
                productName = "Extreme",
                productPrice = Double.MAX_VALUE,
                addedQuantity = Int.MAX_VALUE,
                replenishedQuantity = Int.MIN_VALUE,
                soldCashQuantity = Int.MAX_VALUE,
                soldCartQuantity = Int.MIN_VALUE,
                revenue = Double.MIN_VALUE, // минимальное положительное значение (Double.MIN_VALUE)
            )
        assertEquals("Extreme", extremeView.productName)
        assertEquals(Double.MAX_VALUE, extremeView.productPrice)
        assertEquals(Int.MAX_VALUE, extremeView.addedQuantity)
        assertEquals(Int.MIN_VALUE, extremeView.replenishedQuantity)
        assertEquals(Int.MAX_VALUE, extremeView.soldCashQuantity)
        assertEquals(Int.MIN_VALUE, extremeView.soldCartQuantity)
        assertEquals(Double.MIN_VALUE, extremeView.revenue)
    }
}
