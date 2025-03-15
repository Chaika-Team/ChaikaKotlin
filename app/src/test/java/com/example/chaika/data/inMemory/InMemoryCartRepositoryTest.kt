package com.example.chaika.data.inMemory

import com.example.chaika.domain.models.CartItemDomain
import com.example.chaika.domain.models.ProductInfoDomain
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class InMemoryCartRepositoryTest {

    private lateinit var repository: InMemoryCartRepository

    // Пример данных для тестирования
    private val sampleProduct1 = ProductInfoDomain(
        id = 1,
        name = "Test Product 1",
        description = "Description 1",
        image = "url1",
        price = 10.0
    )

    private val sampleProduct2 = ProductInfoDomain(
        id = 2,
        name = "Test Product 2",
        description = "Description 2",
        image = "url2",
        price = 20.0
    )

    private val cartItem1 = CartItemDomain(
        product = sampleProduct1,
        quantity = 5
    )

    private val cartItem2 = CartItemDomain(
        product = sampleProduct2,
        quantity = 3
    )

    @Before
    fun setup() {
        repository = InMemoryCartRepository()
    }

    @Test
    fun `add item to cart works correctly`() = runTest {
        val added = repository.addItemToCart(cartItem1)
        assertTrue("Item should be added", added)

        // Получаем снимок списка из StateFlow
        val items = repository.getCartItems().first()
        assertEquals(1, items.size)
        assertEquals(cartItem1, items.first())
    }

    @Test
    fun `adding duplicate item should fail`() = runTest {
        val addedFirst = repository.addItemToCart(cartItem1)
        assertTrue("First addition should succeed", addedFirst)
        val addedSecond = repository.addItemToCart(cartItem1)
        assertFalse("Duplicate item should not be added", addedSecond)

        val items = repository.getCartItems().first()
        assertEquals(1, items.size)
    }

    @Test
    fun `remove item from cart works correctly`() = runTest {
        repository.addItemToCart(cartItem1)
        val removed = repository.removeItemFromCart(cartItem1.product.id)
        assertTrue("Item should be removed", removed)
        val items = repository.getCartItems().first()
        assertTrue("Cart should be empty after removal", items.isEmpty())
    }

    @Test
    fun `update item quantity works correctly`() = runTest {
        repository.addItemToCart(cartItem1)
        val newQuantity = 10
        val updated = repository.updateItemQuantity(
            cartItem1.product.id,
            newQuantity,
            availableQuantity = 100
        )
        assertTrue("Quantity update should succeed", updated)
        val items = repository.getCartItems().first()
        assertEquals("Quantity should be updated", newQuantity, items.first().quantity)
    }

    @Test
    fun `clear cart works correctly`() = runTest {
        repository.addItemToCart(cartItem1)
        repository.addItemToCart(cartItem2)
        repository.clearCart()
        val items = repository.getCartItems().first()
        assertTrue("Cart should be empty after clear", items.isEmpty())
    }
}
