package com.example.chaika.data.room.entities

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

/**
 * Тесты для сущности ProductInfo.
 */
class ProductInfoTest {
    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для создания объекта ProductInfo с типичными значениями.
     *   - Классы эквивалентности: корректные данные для name, description, image, price; id по умолчанию = 0.
     */
    @Test
    fun testProductInfoCreation() {
        val productInfo =
            ProductInfo(
                name = "Coffee",
                description = "Arabica coffee",
                image = "coffee.png",
                price = 3.5,
            )
        assertEquals(0, productInfo.id)
        assertEquals("Coffee", productInfo.name)
        assertEquals("Arabica coffee", productInfo.description)
        assertEquals("coffee.png", productInfo.image)
        assertEquals(3.5, productInfo.price)
    }

    /**
     * Техника тест-дизайна: #2 Граничные значения
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для проверки граничных значений для price.
     *   - Граничные значения: проверка цены, равной 0.0, и положительной цены.
     */
    @Test
    fun testProductInfoPriceBoundaries() {
        val freeProduct =
            ProductInfo(
                name = "Water",
                description = "Free water",
                image = "water.png",
                price = 0.0,
            )
        val expensiveProduct =
            ProductInfo(
                name = "Gold Coffee",
                description = "Premium coffee",
                image = "gold_coffee.png",
                price = 999.99,
            )
        assertEquals(0.0, freeProduct.price)
        assertEquals(999.99, expensiveProduct.price)
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для проверки методов equals() и hashCode() для ProductInfo.
     *   - Объекты с одинаковыми параметрами должны быть равны, а при изменении хотя бы одного поля – не равны.
     */
    @Test
    fun testProductInfoEquality() {
        val product1 =
            ProductInfo(
                name = "Tea",
                description = "Green tea",
                image = "tea.png",
                price = 2.0,
            )
        val product2 =
            ProductInfo(
                name = "Tea",
                description = "Green tea",
                image = "tea.png",
                price = 2.0,
            )
        val productDifferent =
            ProductInfo(
                name = "Tea",
                description = "Black tea", // отличается описание
                image = "tea.png",
                price = 2.0,
            )
        assertEquals(product1, product2)
        assertEquals(product1.hashCode(), product2.hashCode())
        assertNotEquals(product1, productDifferent)
    }
}
