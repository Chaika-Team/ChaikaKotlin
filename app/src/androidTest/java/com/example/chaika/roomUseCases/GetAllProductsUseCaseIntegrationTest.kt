package com.example.chaika.roomUseCases

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.chaika.data.room.AppDatabase
import com.example.chaika.data.room.dao.ProductInfoDao
import com.example.chaika.data.room.entities.ProductInfo
import com.example.chaika.domain.models.ProductInfoDomain
import com.example.chaika.domain.usecases.GetAllProductsUseCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.Rule
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class GetAllProductsUseCaseIntegrationTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    // Инжектируем юзкейс, который получает все продукты из базы данных.
    @Inject
    lateinit var getAllProductsUseCase: GetAllProductsUseCase

    // Инжектируем in-memory базу данных и DAO для предварительного наполнения тестовой базы.
    @Inject
    lateinit var appDatabase: AppDatabase

    @Inject
    lateinit var productInfoDao: ProductInfoDao

    @Before
    fun setup() {
        // Выполняем инъекции зависимостей
        hiltRule.inject()
        // Очищаем базу перед тестом
        appDatabase.clearAllTables()
    }

    @After
    fun tearDown() {
        // Закрываем базу после завершения теста
        appDatabase.close()
    }

    @Test
    fun testGetAllProductsUseCaseReturnsInsertedProducts() = runBlocking {
        // Подготавливаем тестовые данные: создаем два продукта.
        val product1 = ProductInfo(
            id = 1,
            name = "Product 1",
            description = "Description 1",
            image = "https://example.com/product1.jpg",
            price = 10.0
        )
        val product2 = ProductInfo(
            id = 2,
            name = "Product 2",
            description = "Description 2",
            image = "https://example.com/product2.jpg",
            price = 20.0
        )
        // Вставляем тестовые данные через DAO
        productInfoDao.insertProduct(product1)
        productInfoDao.insertProduct(product2)

        // Вызываем юзкейс, который возвращает Flow<List<ProductInfoDomain>>
        val result: List<ProductInfoDomain> = getAllProductsUseCase().first()

        // Проверяем, что база вернула два продукта и их данные корректны
        assertEquals("Ожидается 2 продукта в базе", 2, result.size)
        assertEquals("Product 1", result[0].name)
        assertEquals("Product 2", result[1].name)
    }

    @Test
    fun testGetAllProductsUseCaseReturnsEmptyListWhenNoProductsInserted() = runBlocking {
        // Очищаем базу, чтобы не было продуктов
        appDatabase.clearAllTables()

        // Вызываем юзкейс и получаем первое эмитированное значение из Flow
        val result: List<ProductInfoDomain> = getAllProductsUseCase().first()

        // Проверяем, что возвращается пустой список
        assertEquals("Ожидается, что база не содержит продуктов", 0, result.size)
    }

}
