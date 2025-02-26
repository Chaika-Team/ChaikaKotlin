package com.example.chaika.data.room.mappers

import com.example.chaika.data.room.entities.CartItem
import com.example.chaika.data.room.entities.ProductInfo
import com.example.chaika.domain.models.CartItemDomain
import com.example.chaika.domain.models.CartItemReport
import com.example.chaika.domain.models.OperationTypeDomain
import com.example.chaika.domain.models.ProductInfoDomain
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Тесты для мапперов CartItem
 */
class CartItemMapperTest {
    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: John Doe
     *
     * Описание:
     *   - Тест для маппера CartItem.toDomain.
     *   - Классы эквивалентности: корректные данные для impact и productInfo.
     */
    @Test
    fun testCartItemToDomain() {
        // Arrange: создаём тестовые данные
        val productInfo =
            ProductInfo(
                id = 1,
                name = "Test Product",
                description = "Test Description",
                image = "test.png",
                price = 100.0,
            )
        val cartItem =
            CartItem(
                cartOperationId = 10,
                productId = productInfo.id,
                impact = 5,
            )

        // Act: вызываем маппер
        val result: CartItemDomain = cartItem.toDomain(productInfo)

        // Assert: проверяем, что доменная модель содержит корректные данные
        val expectedProductDomain = productInfo.toDomain()
        assertEquals(expectedProductDomain, result.product)
        assertEquals(5, result.quantity)
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: John Doe
     *
     * Описание:
     *   - Тест для маппера CartItemDomain.toEntity с операцией ADD.
     *   - Классы эквивалентности: корректные данные для quantity с операцией ADD (без изменения знака).
     */
    @Test
    fun testCartItemDomainToEntity_AddOperation() {
        // Arrange: создаём тестовый продукт для доменной модели
        val productInfoDomain =
            ProductInfoDomain(
                id = 1,
                name = "Test Product",
                description = "Test Description",
                image = "test.png",
                price = 100.0,
            )
        val cartItemDomain =
            CartItemDomain(
                product = productInfoDomain,
                quantity = 10,
            )
        val cartOperationId = 20

        // Act: преобразуем доменную модель в сущность с операцией ADD
        val result: CartItem = cartItemDomain.toEntity(cartOperationId, OperationTypeDomain.ADD)

        // Assert: для операции ADD количество не изменяется
        assertEquals(cartOperationId, result.cartOperationId)
        assertEquals(productInfoDomain.id, result.productId)
        assertEquals(10, result.impact)
    }

    /**
     * Техника тест-дизайна: #2 Граничные значения
     *
     * Автор: John Doe
     *
     * Описание:
     *   - Тест для маппера CartItemDomain.toEntity с операцией SOLD_CASH.
     *   - Граничные значения: проверка преобразования для положительного значения quantity.
     */
    @Test
    fun testCartItemDomainToEntity_SoldCashOperation() {
        // Arrange: создаём тестовый продукт
        val productInfoDomain =
            ProductInfoDomain(
                id = 2,
                name = "Another Product",
                description = "Another Description",
                image = "another.png",
                price = 200.0,
            )
        val cartItemDomain =
            CartItemDomain(
                product = productInfoDomain,
                quantity = 15,
            )
        val cartOperationId = 30

        // Act: преобразуем доменную модель в сущность с операцией SOLD_CASH
        val result: CartItem =
            cartItemDomain.toEntity(cartOperationId, OperationTypeDomain.SOLD_CASH)

        // Assert: для SOLD_CASH количество должно стать отрицательным
        assertEquals(cartOperationId, result.cartOperationId)
        assertEquals(productInfoDomain.id, result.productId)
        assertEquals(-15, result.impact)
    }

    /**
     * Техника тест-дизайна: #2 Граничные значения
     *
     * Автор: John Doe
     *
     * Описание:
     *   - Тест для маппера CartItemDomain.toEntity с операцией SOLD_CART при количестве равном 0.
     *   - Граничные значения: проверка преобразования для quantity = 0.
     */
    @Test
    fun testCartItemDomainToEntity_SoldCartOperation_ZeroQuantity() {
        // Arrange: создаём тестовый продукт
        val productInfoDomain =
            ProductInfoDomain(
                id = 3,
                name = "Zero Product",
                description = "Zero Description",
                image = "zero.png",
                price = 50.0,
            )
        val cartItemDomain =
            CartItemDomain(
                product = productInfoDomain,
                quantity = 0,
            )
        val cartOperationId = 40

        // Act: преобразуем доменную модель в сущность с операцией SOLD_CART
        val result: CartItem =
            cartItemDomain.toEntity(cartOperationId, OperationTypeDomain.SOLD_CART)

        // Assert: для SOLD_CART при количестве 0 результат остаётся 0 (так как -0 == 0)
        assertEquals(cartOperationId, result.cartOperationId)
        assertEquals(productInfoDomain.id, result.productId)
        assertEquals(0, result.impact)
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: John Doe
     *
     * Описание:
     *   - Тест для маппера CartItemDomain.toEntity с операцией REPLENISH.
     *   - Классы эквивалентности: корректные данные для quantity с операцией REPLENISH (без изменения знака).
     */
    @Test
    fun testCartItemDomainToEntity_ReplenishOperation() {
        // Arrange: создаём тестовый продукт
        val productInfoDomain =
            ProductInfoDomain(
                id = 4,
                name = "Replenish Product",
                description = "Replenish Description",
                image = "replenish.png",
                price = 75.0,
            )
        val cartItemDomain =
            CartItemDomain(
                product = productInfoDomain,
                quantity = 20,
            )
        val cartOperationId = 50

        // Act: преобразуем доменную модель в сущность с операцией REPLENISH
        val result: CartItem =
            cartItemDomain.toEntity(cartOperationId, OperationTypeDomain.REPLENISH)

        // Assert: для REPLENISH количество остаётся без изменений
        assertEquals(cartOperationId, result.cartOperationId)
        assertEquals(productInfoDomain.id, result.productId)
        assertEquals(20, result.impact)
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: John Doe
     *
     * Описание:
     *   - Тест для маппера CartItem.toReport.
     *   - Классы эквивалентности: корректные данные для productID, impact и price.
     */
    @Test
    fun testCartItemToReport() {
        // Arrange: создаём тестовые данные
        val productInfo =
            ProductInfo(
                id = 5,
                name = "Report Product",
                description = "Report Description",
                image = "report.png",
                price = 150.0,
            )
        val cartItem =
            CartItem(
                cartOperationId = 60,
                productId = productInfo.id,
                impact = 7,
            )

        // Act: вызываем маппер для репорт-модели
        val result: CartItemReport = cartItem.toReport(productInfo)

        // Assert: проверяем корректность данных репорт-модели
        assertEquals(productInfo.id, result.productID)
        assertEquals(7, result.quantity)
        assertEquals(150.0, result.price)
    }
}
