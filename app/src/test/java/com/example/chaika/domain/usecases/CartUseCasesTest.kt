@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.chaika.domain.usecases

import com.example.chaika.data.inMemory.InMemoryCartRepositoryInterface
import com.example.chaika.data.room.repo.RoomCartRepositoryInterface
import com.example.chaika.domain.models.CartDomain
import com.example.chaika.domain.models.CartItemDomain
import com.example.chaika.domain.models.CartOperationDomain
import com.example.chaika.domain.models.OperationTypeDomain
import com.example.chaika.domain.models.ProductInfoDomain
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
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

    @Mock
    lateinit var saveOpUseCaseMock: SaveCartWithItemsAndOperationUseCase

    private lateinit var saveCartUseCase: SaveCartWithItemsAndOperationUseCase
    private lateinit var addItemUseCase: AddItemToCartUseCase
    private lateinit var removeItemUseCase: RemoveItemFromCartUseCase
    private lateinit var updateQuantityUseCase: UpdateItemQuantityInCartUseCase
    private lateinit var getCartItemsUseCase: GetCartItemsUseCase

    // новые обёртки
    private lateinit var addOpUseCase: AddOpUseCase
    private lateinit var soldCashOpUseCase: SoldCashOpUseCase
    private lateinit var soldCardOpUseCase: SoldCardOpUseCase
    private lateinit var replenishUseCase: ReplenishUseCase

    // Пример доменных объектов для тестов
    private val dummyCartItems = listOf(
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

        addOpUseCase = AddOpUseCase(saveOpUseCaseMock)
        soldCashOpUseCase = SoldCashOpUseCase(saveOpUseCaseMock)
        soldCardOpUseCase = SoldCardOpUseCase(saveOpUseCaseMock)
        replenishUseCase = ReplenishUseCase(saveOpUseCaseMock)
    }

    /**
     * Тест для SaveCartWithItemsAndOperationUseCase.
     * Проверяется, что:
     * • Получается текущее состояние корзины (CartDomain) из in-memory репозитория.
     * • Вызывается метод сохранения корзины и операции в room-репозитории.
     * • После сохранения вызывается метод очистки in-memory корзины.
     */
    @Test
    fun `SaveCartWithItemsAndOperationUseCase saves cart and clears in-memory cart`() = runTest {
        whenever(inMemoryCartRepo.getCartItems()).thenReturn(flowOf(dummyCartItems))
        saveCartUseCase.invoke(dummyOperation)
        verify(roomCartRepo).saveCartWithItemsAndOperation(
            CartDomain(dummyCartItems),
            dummyOperation
        )
        verify(inMemoryCartRepo).clearCart()
    }

    /**
     * Тест для AddItemToCartUseCase.
     * Проверяется, что при успешном добавлении товара in-memory репозиторий возвращает true.
     */
    @Test
    fun `AddItemToCartUseCase returns true when item added`() {
        val dummyItem = dummyCartItems.first()
        whenever(inMemoryCartRepo.addItemToCart(dummyItem)).thenReturn(true)
        val result = addItemUseCase.invoke(dummyItem)
        assertTrue(result)
    }

    /**
     * Тест для RemoveItemFromCartUseCase.
     * Проверяется, что при успешном удалении товара in-memory репозиторий возвращает true.
     */
    @Test
    fun `RemoveItemFromCartUseCase returns true when item removed`() {
        val itemId = 1
        whenever(inMemoryCartRepo.removeItemFromCart(itemId)).thenReturn(true)
        val result = removeItemUseCase.invoke(itemId)
        assertTrue(result)
    }

    /**
     * Тест для UpdateItemQuantityInCartUseCase.
     * Проверяется, что обновление количества товара проходит успешно.
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
     * Тест для GetCartItemsUseCase.
     * Проверяется, что use case возвращает корректный список товаров из in-memory репозитория.
     */
    @Test
    fun `GetCartItemsUseCase returns list of cart items`() = runTest {
        whenever(inMemoryCartRepo.getCartItems()).thenReturn(flowOf(dummyCartItems))
        // Получаем снимок списка из Flow
        val result = getCartItemsUseCase.invoke().first()
        assertEquals(dummyCartItems, result)
    }

    /**
     * Тест для AddOpUseCase.
     * Проверяется, что при вызове invoke(conductorId)
     * базовый SaveCartWithItemsAndOperationUseCase
     * получает CartOperationDomain с типом ADD и тем же conductorId.
     */
    @Test
    fun `AddOpUseCase invokes saveOpUseCase with ADD`() = runTest {
        val conductorId = 42
        val expectedOp = CartOperationDomain(OperationTypeDomain.ADD, conductorId)

        addOpUseCase.invoke(conductorId)

        verify(saveOpUseCaseMock).invoke(expectedOp)
    }

    /**
     * Тест для SoldCashOpUseCase.
     * Проверяется, что при вызове invoke(conductorId)
     * базовый SaveCartWithItemsAndOperationUseCase
     * получает CartOperationDomain с типом SOLD_CASH и тем же conductorId.
     */
    @Test
    fun `SoldCashOpUseCase invokes saveOpUseCase with SOLD_CASH`() = runTest {
        val conductorId = 43
        val expectedOp = CartOperationDomain(OperationTypeDomain.SOLD_CASH, conductorId)

        soldCashOpUseCase.invoke(conductorId)

        verify(saveOpUseCaseMock).invoke(expectedOp)
    }

    /**
     * Тест для SoldCardOpUseCase.
     * Проверяется, что при вызове invoke(conductorId)
     * базовый SaveCartWithItemsAndOperationUseCase
     * получает CartOperationDomain с типом SOLD_CART и тем же conductorId.
     */
    @Test
    fun `SoldCardOpUseCase invokes saveOpUseCase with SOLD_CART`() = runTest {
        val conductorId = 44
        val expectedOp = CartOperationDomain(OperationTypeDomain.SOLD_CART, conductorId)

        soldCardOpUseCase.invoke(conductorId)

        verify(saveOpUseCaseMock).invoke(expectedOp)
    }

    /**
     * Тест для ReplenishUseCase.
     * Проверяется, что при вызове invoke(conductorId)
     * базовый SaveCartWithItemsAndOperationUseCase
     * получает CartOperationDomain с типом REPLENISH и тем же conductorId.
     */
    @Test
    fun `ReplenishUseCase invokes saveOpUseCase with REPLENISH`() = runTest {
        val conductorId = 45
        val expectedOp = CartOperationDomain(OperationTypeDomain.REPLENISH, conductorId)

        replenishUseCase.invoke(conductorId)

        verify(saveOpUseCaseMock).invoke(expectedOp)
    }
}
