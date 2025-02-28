@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.example.chaika.domain.usecases

import com.example.chaika.data.inMemory.InMemoryCartRepositoryInterface
import com.example.chaika.data.room.repo.RoomCartRepositoryInterface
import com.example.chaika.domain.models.CartDomain
import com.example.chaika.domain.models.CartItemDomain
import com.example.chaika.domain.models.CartOperationDomain
import com.example.chaika.domain.models.OperationTypeDomain
import com.example.chaika.domain.models.ProductInfoDomain
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class CartUseCasesTest {

    @Mock
    lateinit var inMemoryCartRepo: InMemoryCartRepositoryInterface

    @Mock
    lateinit var roomCartRepo: RoomCartRepositoryInterface

    private lateinit var saveCartUseCase: SaveCartWithItemsAndOperationUseCase
    private lateinit var addItemUseCase: AddItemToCartUseCase
    private lateinit var removeItemUseCase: RemoveItemFromCartUseCase
    private lateinit var updateQuantityUseCase: UpdateItemQuantityInCartUseCase
    private lateinit var getCartItemsUseCase: GetCartItemsUseCase

    // Пример доменных объектов для тестов
    private val dummyCartItems = mutableListOf(
        CartItemDomain(
            product = ProductInfoDomain(1, "Product 1", "Desc", "img", 10.0),
            quantity = 2
        ),
        CartItemDomain(
            product = ProductInfoDomain(2, "Product 2", "Desc", "img", 15.0),
            quantity = 3
        )
    )

    private val dummyOperation = CartOperationDomain(
        operationTypeDomain = OperationTypeDomain.ADD,
        conductorId = 123
    )

    @BeforeEach
    fun setUp() {
        saveCartUseCase = SaveCartWithItemsAndOperationUseCase(roomCartRepo, inMemoryCartRepo)
        addItemUseCase = AddItemToCartUseCase(inMemoryCartRepo)
        removeItemUseCase = RemoveItemFromCartUseCase(inMemoryCartRepo)
        updateQuantityUseCase = UpdateItemQuantityInCartUseCase(inMemoryCartRepo)
        getCartItemsUseCase = GetCartItemsUseCase(inMemoryCartRepo)
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: OwletsFox
     *
     * Описание:
     *   - Тест для SaveCartWithItemsAndOperationUseCase.
     *   - Проверяется, что:
     *       • Текущая корзина (CartDomain) получается из in-memory репозитория.
     *       • Вызывается метод сохранения корзины и операции в room-репозитории.
     *       • После сохранения вызывается метод очистки in-memory корзины.
     */
    @Test
    fun `SaveCartWithItemsAndOperationUseCase saves cart and clears in-memory cart`() = runTest {
        whenever(inMemoryCartRepo.getCartItems()).thenReturn(dummyCartItems)
        saveCartUseCase.invoke(dummyOperation)
        verify(roomCartRepo).saveCartWithItemsAndOperation(
            CartDomain(dummyCartItems),
            dummyOperation
        )
        verify(inMemoryCartRepo).clearCart()
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: OwletsFox
     *
     * Описание:
     *   - Тест для AddItemToCartUseCase.
     *   - Проверяется, что при успешном добавлении товара in-memory репозиторий возвращает true.
     */
    @Test
    fun `AddItemToCartUseCase returns true when item added`() {
        val dummyItem = dummyCartItems.first()
        whenever(inMemoryCartRepo.addItemToCart(dummyItem)).thenReturn(true)
        val result = addItemUseCase.invoke(dummyItem)
        assertTrue(result)
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: OwletsFox
     *
     * Описание:
     *   - Тест для RemoveItemFromCartUseCase.
     *   - Проверяется, что при успешном удалении товара in-memory репозиторий возвращает true.
     */
    @Test
    fun `RemoveItemFromCartUseCase returns true when item removed`() {
        val itemId = 1
        whenever(inMemoryCartRepo.removeItemFromCart(itemId)).thenReturn(true)
        val result = removeItemUseCase.invoke(itemId)
        assertTrue(result)
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: OwletsFox
     *
     * Описание:
     *   - Тест для UpdateItemQuantityInCartUseCase.
     *   - Проверяется, что обновление количества товара проходит успешно (метод возвращает true).
     */
    @Test
    fun `UpdateItemQuantityInCartUseCase returns true when quantity updated`() {
        val itemId = 1
        val newQuantity = 5
        val availableQuantity = 10
        whenever(
            inMemoryCartRepo.updateItemQuantity(
                itemId,
                newQuantity,
                availableQuantity
            )
        ).thenReturn(true)
        val result = updateQuantityUseCase.invoke(itemId, newQuantity, availableQuantity)
        assertTrue(result)
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: OwletsFox
     *
     * Описание:
     *   - Тест для GetCartItemsUseCase.
     *   - Проверяется, что use case возвращает список товаров из in-memory репозитория.
     */
    @Test
    fun `GetCartItemsUseCase returns list of cart items`() {
        whenever(inMemoryCartRepo.getCartItems()).thenReturn(dummyCartItems)
        val result = getCartItemsUseCase.invoke()
        assertEquals(dummyCartItems, result)
    }
}
