package com.example.chaika.data.room.mappers

import com.example.chaika.data.room.entities.CartItem
import com.example.chaika.data.room.entities.ProductInfo
import com.example.chaika.data.room.relations.CartItemWithProduct
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Тесты для маппера List<CartItemWithProduct> -> CartDomain.
 */
class OperationItemsMapperTest {

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: OwletsFox
     *
     * Описание:
     *   - Корректное преобразование одной позиции корзины с продуктом в CartDomain.
     *   - Проверяются поля продукта и количество (impact).
     */
    @Test
    fun testListCartItemWithProduct_toCartDomain_single() {
        // Arrange
        val product = ProductInfo(
            id = 11, name = "Water", description = "Still 0.5L",
            image = "img://water", price = 1.5
        )
        val item = CartItem(
            id = 101, cartOperationId = 100, productId = product.id, impact = 3
        )
        val list = listOf(CartItemWithProduct(item = item, product = product))

        // Act
        val cart = list.toCartDomain()

        // Assert
        assertEquals(1, cart.items.size)
        val cartItem = cart.items.first()
        assertEquals(11, cartItem.product.id)
        assertEquals("Water", cartItem.product.name)
        assertEquals("Still 0.5L", cartItem.product.description)
        assertEquals("img://water", cartItem.product.image)
        assertEquals(1.5, cartItem.product.price)
        assertEquals(3, cartItem.quantity)
    }

    /**
     * Техника тест-дизайна: #2 Граничные значения
     *
     * Автор: OwletsFox
     *
     * Описание:
     *   - Пустой список CartItemWithProduct должен маппиться в пустую корзину без ошибок.
     */
    @Test
    fun testListCartItemWithProduct_toCartDomain_empty() {
        // Arrange
        val list = emptyList<CartItemWithProduct>()

        // Act
        val cart = list.toCartDomain()

        // Assert
        assertEquals(0, cart.items.size)
    }
}
