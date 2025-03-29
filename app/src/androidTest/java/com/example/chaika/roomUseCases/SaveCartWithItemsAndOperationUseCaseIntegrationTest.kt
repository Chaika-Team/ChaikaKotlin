package com.example.chaika.roomUseCases

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.chaika.data.inMemory.InMemoryCartRepositoryInterface
import com.example.chaika.data.room.AppDatabase
import com.example.chaika.data.room.dao.CartItemDao
import com.example.chaika.data.room.dao.CartOperationDao
import com.example.chaika.data.room.dao.ConductorDao
import com.example.chaika.data.room.dao.ProductInfoDao
import com.example.chaika.data.room.entities.Conductor
import com.example.chaika.data.room.entities.ProductInfo
import com.example.chaika.domain.models.CartItemDomain
import com.example.chaika.domain.models.CartOperationDomain
import com.example.chaika.domain.models.OperationTypeDomain
import com.example.chaika.domain.models.ProductInfoDomain
import com.example.chaika.domain.usecases.SaveCartWithItemsAndOperationUseCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.Rule
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class SaveCartWithItemsAndOperationUseCaseIntegrationTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    // Юзкейс для сохранения корзины и операции
    @Inject
    lateinit var saveCartWithItemsAndOperationUseCase: SaveCartWithItemsAndOperationUseCase

    // in-memory репозиторий для корзины
    @Inject
    lateinit var inMemoryCartRepository: InMemoryCartRepositoryInterface

    // База данных и DAO для проверки сохранённых данных
    @Inject
    lateinit var appDatabase: AppDatabase

    @Inject
    lateinit var cartOperationDao: CartOperationDao

    @Inject
    lateinit var cartItemDao: CartItemDao

    @Inject
    lateinit var productInfoDao: ProductInfoDao

    @Inject
    lateinit var conductorDao: ConductorDao

    @Before
    fun setup() {
        hiltRule.inject()
        // Очищаем in-memory корзину и базу перед каждым тестом
        inMemoryCartRepository.clearCart()
        appDatabase.clearAllTables()
    }

    @After
    fun tearDown() {
        // Закрываем базу после теста
        appDatabase.close()
    }

    @Test
    fun testSaveCartWithItemsAndOperationUseCaseSavesDataAndClearsInMemoryCart() = runBlocking {
        // Подготавливаем данные для внешних ключей:
        // Вставляем проводника с id = 1
        conductorDao.insertConductor(
            Conductor(
                id = 1,
                name = "Test",
                familyName = "Conductor",
                givenName = "Test",
                employeeID = "1",
                image = "https://example.com/conductor.jpg"
            )
        )
        // Вставляем товары с id 101 и 102
        productInfoDao.insertProduct(
            ProductInfo(
                id = 101,
                name = "Test Product 1",
                description = "Test Description 1",
                image = "https://example.com/product101.jpg",
                price = 15.0
            )
        )
        productInfoDao.insertProduct(
            ProductInfo(
                id = 102,
                name = "Test Product 2",
                description = "Test Description 2",
                image = "https://example.com/product102.jpg",
                price = 25.0
            )
        )

        // Создаем элементы корзины, ссылающиеся на подготовленные товары
        val item1 = CartItemDomain(
            product = ProductInfoDomain(
                id = 101,
                name = "Test Product 1",
                description = "Test Description 1",
                image = "https://example.com/product101.jpg",
                price = 15.0
            ),
            quantity = 2
        )
        val item2 = CartItemDomain(
            product = ProductInfoDomain(
                id = 102,
                name = "Test Product 2",
                description = "Test Description 2",
                image = "https://example.com/product102.jpg",
                price = 25.0
            ),
            quantity = 3
        )

        // Добавляем элементы в in-memory корзину
        inMemoryCartRepository.addItemToCart(item1)
        inMemoryCartRepository.addItemToCart(item2)

        // Создаем операцию для корзины с существующим проводником (id = 1)
        val cartOperationDomain = CartOperationDomain(
            operationTypeDomain = OperationTypeDomain.ADD,
            conductorId = 1
        )

        // Вызываем юзкейс: он должен сохранить корзину и операцию в базе данных, а затем очистить in-memory корзину
        saveCartWithItemsAndOperationUseCase(cartOperationDomain)

        // Проверяем, что in-memory корзина очищена
        val inMemoryItemsAfter = inMemoryCartRepository.getCartItems().first()
        assertEquals("In-memory корзина должна быть очищена", 0, inMemoryItemsAfter.size)

        // Проверяем данные в базе:
        // 1. Получаем сохраненную операцию через DAO
        val operationsList = cartOperationDao.getAllOperations().first()
        assertEquals("Ожидается 1 операция в базе", 1, operationsList.size)

        // 2. Получаем сохраненные элементы корзины через DAO
        val cartItemsList = cartItemDao.getAllCartItems().first()
        assertEquals("Ожидается 2 элемента корзины в базе", 2, cartItemsList.size)

        // Проверяем, что каждый элемент корзины ссылается на сохраненную операцию
        val operationId = operationsList[0].id
        cartItemsList.forEach { cartItem ->
            assertEquals(
                "Элемент корзины должен ссылаться на сохраненную операцию",
                operationId,
                cartItem.cartOperationId
            )
        }
    }

    @Test(expected = Exception::class)
    fun testSaveCartFailsWhenConductorNotFound() = runBlocking {
        // Подготавливаем данные для внешних ключей:
        // В данном тесте намеренно не вставляем проводника с id = 1

        // Вставляем товар с id 101
        productInfoDao.insertProduct(
            ProductInfo(
                id = 101,
                name = "Test Product 1",
                description = "Test Description 1",
                image = "https://example.com/product101.jpg",
                price = 15.0
            )
        )

        // Добавляем элемент в in-memory корзину, ссылающийся на товар id 101
        val item = CartItemDomain(
            product = ProductInfoDomain(
                id = 101,
                name = "Test Product 1",
                description = "Test Description 1",
                image = "https://example.com/product101.jpg",
                price = 15.0
            ),
            quantity = 2
        )
        inMemoryCartRepository.addItemToCart(item)

        // Создаем операцию для корзины с conductorId = 1, которого нет в базе
        val cartOperationDomain = CartOperationDomain(
            operationTypeDomain = OperationTypeDomain.ADD,
            conductorId = 1
        )

        // Ожидается, что вызов юзкейса выбросит Exception из-за нарушения ограничения внешнего ключа
        saveCartWithItemsAndOperationUseCase(cartOperationDomain)
        fail("Expected an Exception to be thrown due to missing conductor record")
    }
}
