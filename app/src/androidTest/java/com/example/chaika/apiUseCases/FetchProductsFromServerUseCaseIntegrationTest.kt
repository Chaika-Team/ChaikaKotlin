package com.example.chaika.apiUseCases

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.chaika.domain.models.ProductInfoDomain
import com.example.chaika.domain.usecases.FetchProductsFromServerUseCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import testUtils.TestMockServer
import testUtils.TestServerHolder
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class FetchProductsFromServerUseCaseIntegrationTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    // Инжектируем use case для получения списка товаров с сервера
    @Inject
    lateinit var fetchProductsFromServerUseCase: FetchProductsFromServerUseCase

    @Before
    fun setUp() {
        // Запускаем новый экземпляр тестового сервера для тестов
        val testServer = TestMockServer().apply { start() }
        TestServerHolder.testMockServer = testServer

        hiltRule.inject()
    }

    @After
    fun tearDown() {
        // Останавливаем тестовый сервер
        TestServerHolder.testMockServer.shutdown()
    }

    @Test
    fun testServerReturnsProductsSuccessfully() = runTest {
        // Подготавливаем корректный JSON-ответ с двумя товарами
        val responseBody = """{
            "products": [
                {
                    "id": 1,
                    "name": "Product 1",
                    "description": "Description 1",
                    "imageurl": "https://example.com/product1.jpg",
                    "price": 10.0
                },
                {
                    "id": 2,
                    "name": "Product 2",
                    "description": "Description 2",
                    "imageurl": "https://example.com/product2.jpg",
                    "price": 20.0
                }
            ]
        }"""
        TestServerHolder.testMockServer.server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(responseBody)
        )

        // Вызываем use case для получения товаров с сервера
        val result: List<ProductInfoDomain> =
            fetchProductsFromServerUseCase(limit = 100, offset = 0)

        // Проверяем, что возвращается два товара
        assertEquals("Expected 2 products", 2, result.size)
    }

    @Test(expected = Exception::class)
    fun testServerErrorForFetchProducts() = runTest {
        // Эмулируем серверную ошибку
        TestServerHolder.testMockServer.server.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setHeader("Content-Type", "application/json")
                .setBody("")
        )
        // Ожидается, что use case выбросит исключение при ошибке сервера
        fetchProductsFromServerUseCase(limit = 100, offset = 0)
    }
}
