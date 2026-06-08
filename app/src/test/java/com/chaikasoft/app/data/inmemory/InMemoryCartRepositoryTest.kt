package com.chaikasoft.app.data.inmemory

import com.chaikasoft.app.domain.models.CartItemDomain
import com.chaikasoft.app.domain.models.ProductInfoDomain
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest

class InMemoryCartRepositoryTest : FunSpec({

    lateinit var repository: InMemoryCartRepository

    val tea = ProductInfoDomain(
        id = 1,
        name = "Tea",
        description = "Black",
        image = "img",
        price = 150,
    )
    val coffee = ProductInfoDomain(
        id = 2,
        name = "Coffee",
        description = "Arabica",
        image = "img2",
        price = 220,
    )

    beforeTest {
        repository = InMemoryCartRepository()
    }

    /**
     * Техника тест-дизайна: #2 Граничные значения.
     *
     * Описание:
     *   - Пустая корзина должна иметь пустой список элементов при первом чтении Flow.
     */
    test("initial cart items flow is empty") {
        runTest {
            repository.getCartItems().first() shouldBe emptyList()
        }
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности.
     *
     * Описание:
     *   - Новый товар с положительным количеством добавляется в корзину без изменений quantity.
     */
    test("addItemToCart adds new item with positive quantity as is") {
        runTest {
            val added = repository.addItemToCart(
                CartItemDomain(product = tea, quantity = 3),
            )

            added shouldBe true
            repository.getCartItems().first() shouldBe listOf(
                CartItemDomain(product = tea, quantity = 3),
            )
        }
    }

    /**
     * Техника тест-дизайна: #2 Граничные значения.
     *
     * Описание:
     *   - Для нового товара с quantity <= 0 применяется fallback quantity = 1.
     */
    test("addItemToCart uses fallback quantity one when incoming quantity is non-positive") {
        runTest {
            repository.addItemToCart(CartItemDomain(product = tea, quantity = 0)) shouldBe true
            repository.getCartItems().first() shouldBe listOf(
                CartItemDomain(product = tea, quantity = 1),
            )
        }
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности.
     *
     * Описание:
     *   - Дубликат по product.id не должен повторно добавляться.
     */
    test("addItemToCart rejects duplicate product id") {
        runTest {
            repository.addItemToCart(CartItemDomain(product = tea, quantity = 2)) shouldBe true

            repository.addItemToCart(
                CartItemDomain(
                    product = tea.copy(name = "Tea changed"),
                    quantity = 9,
                ),
            ) shouldBe false

            repository.getCartItems().first() shouldBe listOf(
                CartItemDomain(product = tea, quantity = 2),
            )
        }
    }

    /**
     * Техника тест-дизайна: #7 Таблица решений.
     *
     * Описание:
     *   - removeItemFromCart возвращает true только когда элемент действительно был в корзине.
     */
    test("removeItemFromCart returns true for existing item and false for missing item") {
        runTest {
            repository.addItemToCart(CartItemDomain(product = tea, quantity = 1))
            repository.removeItemFromCart(tea.id) shouldBe true
            repository.getCartItems().first() shouldBe emptyList()

            repository.removeItemFromCart(tea.id) shouldBe false
        }
    }

    /**
     * Техника тест-дизайна: #7 Таблица решений.
     *
     * Описание:
     *   - updateItemQuantity успешен только для существующего товара и допустимого количества.
     */
    test("updateItemQuantity updates existing item when quantity is valid and available") {
        runTest {
            repository.addItemToCart(CartItemDomain(product = tea, quantity = 1))

            repository.updateItemQuantity(
                itemId = tea.id,
                newQuantity = 5,
                availableQuantity = 10,
            ) shouldBe true

            repository.getCartItems().first() shouldBe listOf(
                CartItemDomain(product = tea, quantity = 5),
            )
        }
    }

    /**
     * Техника тест-дизайна: #2 Граничные значения.
     *
     * Описание:
     *   - При newQuantity > availableQuantity обновление запрещается и состояние не меняется.
     */
    test("updateItemQuantity returns false and keeps state when new quantity exceeds available") {
        runTest {
            repository.addItemToCart(CartItemDomain(product = tea, quantity = 2))

            repository.updateItemQuantity(
                itemId = tea.id,
                newQuantity = 11,
                availableQuantity = 10,
            ) shouldBe false

            repository.getCartItems().first() shouldBe listOf(
                CartItemDomain(product = tea, quantity = 2),
            )
        }
    }

    /**
     * Техника тест-дизайна: #5 Error Guessing.
     *
     * Описание:
     *   - Обновление отсутствующего товара возвращает false и не меняет корзину.
     */
    test("updateItemQuantity returns false for unknown item id") {
        runTest {
            repository.addItemToCart(CartItemDomain(product = tea, quantity = 2))

            repository.updateItemQuantity(
                itemId = coffee.id,
                newQuantity = 1,
                availableQuantity = 10,
            ) shouldBe false

            repository.getCartItems().first() shouldBe listOf(
                CartItemDomain(product = tea, quantity = 2),
            )
        }
    }

    /**
     * Техника тест-дизайна: #2 Граничные значения.
     *
     * Описание:
     *   - При newQuantity = 0 элемент удаляется из корзины.
     *   - Результат отражает успешность удаления.
     */
    test("updateItemQuantity with zero quantity removes item from cart") {
        runTest {
            repository.addItemToCart(CartItemDomain(product = tea, quantity = 2))

            repository.updateItemQuantity(
                itemId = tea.id,
                newQuantity = 0,
                availableQuantity = 10,
            ) shouldBe true

            repository.getCartItems().first() shouldBe emptyList()
        }
    }

    test("updateItemQuantity with negative quantity keeps item in cart") {
        runTest {
            repository.addItemToCart(CartItemDomain(product = tea, quantity = 2))

            repository.updateItemQuantity(
                itemId = tea.id,
                newQuantity = -1,
                availableQuantity = 10,
            ) shouldBe false

            repository.getCartItems().first() shouldBe listOf(
                CartItemDomain(product = tea, quantity = 2),
            )
        }
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности.
     *
     * Описание:
     *   - clearCart всегда приводит корзину к пустому состоянию.
     */
    test("clearCart removes all items") {
        runTest {
            repository.addItemToCart(CartItemDomain(product = tea, quantity = 2))
            repository.addItemToCart(CartItemDomain(product = coffee, quantity = 1))

            repository.clearCart()

            repository.getCartItems().first() shouldBe emptyList()
        }
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности.
     *
     * Описание:
     *   - getCart должен возвращать снимок списка, а не "живую" ссылку на внутреннее хранилище.
     */
    test("getCart returns snapshot copy of current items") {
        runTest {
            repository.addItemToCart(CartItemDomain(product = tea, quantity = 1))
            val snapshot = repository.getCart()

            repository.addItemToCart(CartItemDomain(product = coffee, quantity = 1))

            snapshot.items shouldBe listOf(CartItemDomain(product = tea, quantity = 1))
            repository.getCart().items.size shouldBe 2
        }
    }
})

