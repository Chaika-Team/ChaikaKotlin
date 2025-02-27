package com.example.chaika.data.inMemory

import com.example.chaika.domain.models.CartItemDomain
import com.example.chaika.domain.models.ProductInfoDomain
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Тесты для InMemoryCartRepository.
 */
class InMemoryCartRepositoryTest {
    private lateinit var repository: InMemoryCartRepository

    @BeforeEach
    fun setup() {
        repository = InMemoryCartRepository()
        // Очищаем корзину перед каждым тестом
        repository.clearCart()
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: John Doe
     *
     * Описание:
     *   - Тест для addItemToCart: позитивный сценарий, когда товар отсутствует в корзине.
     *   - Классы эквивалентности: добавление нового товара должно вернуть true и увеличить размер списка.
     */
    @Test
    fun testAddItemToCart_itemNotPresent() =
        runBlocking {
            // Arrange: создаем CartItemDomain с уникальным product.id
            val item =
                CartItemDomain(
                    product = ProductInfoDomain(1, "Product 1", "Desc1", "img1.png", 10.0),
                    quantity = 5,
                )

            // Act: добавляем товар в корзину
            val result = repository.addItemToCart(item)

            // Assert: результат должен быть true, и список должен содержать 1 элемент
            assertTrue(result, "Добавление нового товара должно возвращать true")
            assertEquals(1, repository.getCartItems().size, "Размер корзины должен быть равен 1")
        }

    /**
     * Техника тест-дизайна: #4 Прогнозирование ошибок / Причинно-следственный анализ
     *
     * Автор: John Doe
     *
     * Описание:
     *   - Тест для addItemToCart: негативный сценарий, когда товар уже существует в корзине.
     *   - Если товар с таким же product.id уже добавлен, метод должен вернуть false.
     */
    @Test
    fun testAddItemToCart_itemAlreadyPresent() =
        runBlocking {
            // Arrange: добавляем товар в корзину
            val item =
                CartItemDomain(
                    product = ProductInfoDomain(1, "Product 1", "Desc1", "img1.png", 10.0),
                    quantity = 5,
                )
            repository.addItemToCart(item)

            // Act: пытаемся добавить тот же товар повторно
            val result = repository.addItemToCart(item)

            // Assert: результат должен быть false, и размер корзины не должен измениться
            assertFalse(result, "Попытка добавить уже существующий товар должна возвращать false")
            assertEquals(
                1,
                repository.getCartItems().size,
                "Размер корзины должен оставаться равным 1",
            )
        }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: John Doe
     *
     * Описание:
     *   - Тест для removeItemFromCart: позитивный сценарий, когда товар существует.
     *   - Если товар с заданным product.id присутствует, метод должен вернуть true и удалить его.
     */
    @Test
    fun testRemoveItemFromCart_itemExists() =
        runBlocking {
            // Arrange: добавляем товар в корзину
            val item =
                CartItemDomain(
                    product = ProductInfoDomain(2, "Product 2", "Desc2", "img2.png", 20.0),
                    quantity = 3,
                )
            repository.addItemToCart(item)

            // Act: удаляем товар по product.id
            val result = repository.removeItemFromCart(2)

            // Assert: результат должен быть true, и корзина должна стать пустой
            assertTrue(result, "Удаление существующего товара должно возвращать true")
            assertEquals(
                0,
                repository.getCartItems().size,
                "После удаления корзина должна быть пустой",
            )
        }

    /**
     * Техника тест-дизайна: #4 Прогнозирование ошибок / Причинно-следственный анализ
     *
     * Автор: John Doe
     *
     * Описание:
     *   - Тест для removeItemFromCart: негативный сценарий, когда товар не найден.
     *   - Если товар с заданным product.id отсутствует, метод должен вернуть false.
     */
    @Test
    fun testRemoveItemFromCart_itemNotFound() =
        runBlocking {
            // Act: пытаемся удалить товар, которого нет в корзине
            val result = repository.removeItemFromCart(999)

            // Assert: результат должен быть false
            assertFalse(result, "Удаление несуществующего товара должно возвращать false")
        }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: John Doe
     *
     * Описание:
     *   - Тест для updateItemQuantity: позитивный сценарий, когда товар найден и новое количество не превышает availableQuantity.
     *   - Классы эквивалентности: корректное обновление количества и возврат true.
     */
    @Test
    fun testUpdateItemQuantity_success() =
        runBlocking {
            // Arrange: добавляем товар в корзину
            val item =
                CartItemDomain(
                    product = ProductInfoDomain(3, "Product 3", "Desc3", "img3.png", 30.0),
                    quantity = 2,
                )
            repository.addItemToCart(item)

            // Act: обновляем количество до 4, при условии, что availableQuantity = 5
            val result =
                repository.updateItemQuantity(itemId = 3, newQuantity = 4, availableQuantity = 5)

            // Assert: обновление должно вернуть true, а количество товара должно быть равно 4
            assertTrue(result, "Обновление количества товара должно возвращать true")
            assertEquals(4, repository.getCartItems().first { it.product.id == 3 }.quantity)
        }

    /**
     * Техника тест-дизайна: #2 Граничные значения
     *
     * Автор: John Doe
     *
     * Описание:
     *   - Тест для updateItemQuantity: негативный сценарий, когда новое количество превышает availableQuantity.
     *   - Граничные значения: если newQuantity > availableQuantity, метод должен вернуть false и количество не обновляется.
     */
    @Test
    fun testUpdateItemQuantity_exceedsAvailable() =
        runBlocking {
            // Arrange: добавляем товар в корзину
            val item =
                CartItemDomain(
                    product = ProductInfoDomain(4, "Product 4", "Desc4", "img4.png", 40.0),
                    quantity = 2,
                )
            repository.addItemToCart(item)

            // Act: пытаемся обновить количество до 5 при availableQuantity = 4
            val result =
                repository.updateItemQuantity(itemId = 4, newQuantity = 5, availableQuantity = 4)

            // Assert: обновление должно вернуть false, а исходное количество должно сохраниться
            assertFalse(
                result,
                "Если новое количество превышает availableQuantity, метод должен вернуть false",
            )
            assertEquals(2, repository.getCartItems().first { it.product.id == 4 }.quantity)
        }

    /**
     * Техника тест-дизайна: #4 Прогнозирование ошибок / Причинно-следственный анализ
     *
     * Автор: John Doe
     *
     * Описание:
     *   - Тест для updateItemQuantity: негативный сценарий, когда товар не найден.
     *   - Если товар с заданным product.id отсутствует, метод должен вернуть false.
     */
    @Test
    fun testUpdateItemQuantity_itemNotFound() =
        runBlocking {
            // Act: пытаемся обновить количество для товара, которого нет в корзине
            val result =
                repository.updateItemQuantity(itemId = 999, newQuantity = 3, availableQuantity = 5)

            // Assert: метод должен вернуть false
            assertFalse(result, "Если товар не найден, метод должен вернуть false")
        }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: John Doe
     *
     * Описание:
     *   - Тест для getCartItems: проверка корректного возврата списка товаров.
     *   - Классы эквивалентности: после добавления нескольких товаров метод должен вернуть список с корректными значениями.
     */
    @Test
    fun testGetCartItems() =
        runBlocking {
            // Arrange: добавляем несколько товаров
            val item1 =
                CartItemDomain(
                    product = ProductInfoDomain(5, "Product 5", "Desc5", "img5.png", 50.0),
                    quantity = 1,
                )
            val item2 =
                CartItemDomain(
                    product = ProductInfoDomain(6, "Product 6", "Desc6", "img6.png", 60.0),
                    quantity = 2,
                )
            repository.addItemToCart(item1)
            repository.addItemToCart(item2)

            // Act: получаем список товаров
            val items = repository.getCartItems()

            // Assert: проверяем, что список содержит два товара с корректными данными
            assertEquals(2, items.size, "Список товаров должен содержать 2 элемента")
            assertEquals(5, items[0].product.id)
            assertEquals(1, items[0].quantity)
            assertEquals(6, items[1].product.id)
            assertEquals(2, items[1].quantity)
        }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: John Doe
     *
     * Описание:
     *   - Тест для clearCart: проверка очистки корзины.
     *   - Классы эквивалентности: после вызова clearCart список товаров должен быть пуст.
     */
    @Test
    fun testClearCart() =
        runBlocking {
            // Arrange: добавляем товары в корзину
            val item =
                CartItemDomain(
                    product = ProductInfoDomain(7, "Product 7", "Desc7", "img7.png", 70.0),
                    quantity = 3,
                )
            repository.addItemToCart(item)

            // Act: очищаем корзину
            repository.clearCart()

            // Assert: список товаров должен быть пуст
            assertEquals(
                0,
                repository.getCartItems().size,
                "После очистки корзина должна быть пустой",
            )
        }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: John Doe
     *
     * Описание:
     *   - Тест для getCart: проверка, что метод возвращает один и тот же объект корзины.
     *   - Классы эквивалентности: объект корзины, возвращаемый getCart, совпадает с внутренним состоянием репозитория.
     */
    @Test
    fun testGetCart() =
        runBlocking {
            // Arrange: получаем объект корзины
            val cart1 = repository.getCart()

            // Act: добавляем товар через метод addItemToCart
            val item =
                CartItemDomain(
                    product = ProductInfoDomain(8, "Product 8", "Desc8", "img8.png", 80.0),
                    quantity = 2,
                )
            repository.addItemToCart(item)
            val cart2 = repository.getCart()

            // Assert: объект корзины должен оставаться тем же самым, а его содержимое изменяться
            assertSame(cart1, cart2, "Метод getCart должен возвращать один и тот же объект корзины")
            assertEquals(1, cart2.items.size, "Количество товаров в корзине должно быть равно 1")
        }
}
