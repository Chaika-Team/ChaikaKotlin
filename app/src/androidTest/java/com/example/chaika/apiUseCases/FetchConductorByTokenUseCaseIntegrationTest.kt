package com.example.chaika.apiUseCases

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.chaika.domain.models.ConductorDomain
import com.example.chaika.domain.usecases.FetchConductorByTokenUseCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import testUtils.TestMockServer
import testUtils.TestServerHolder
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class FetchConductorByTokenUseCaseIntegrationTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var fetchConductorByTokenUseCase: FetchConductorByTokenUseCase

    @Before
    fun setUp() {
        // Запускаем новый экземпляр тестового сервера и сохраняем его в холдер
        val testServer = TestMockServer().apply { start() }
        TestServerHolder.testMockServer = testServer

        // Выполняем инъекцию зависимостей
        hiltRule.inject()
    }

    @After
    fun tearDown() {
        // Останавливаем тестовый сервер после тестов
        TestServerHolder.testMockServer.shutdown()
    }

    @Test
    fun testServerReturnsUserInfoSuccessfully() = runTest {
        // Подготавливаем корректный JSON-ответ, который соответствует ConductorDto
        val responseBody = """
            {
              "name": "John",
              "family_name": "Doe",
              "given_name": "John",
              "nickname": "12345",
              "image": "https://example.com/johndoe.jpg"
            }
        """.trimIndent()
        TestServerHolder.testMockServer.server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(responseBody)
        )

        // Вызываем use case и проверяем результат
        val result: ConductorDomain = fetchConductorByTokenUseCase("dummyToken")
        assertEquals("John", result.name)
        assertEquals("Doe", result.familyName)
        assertEquals("John", result.givenName)
        assertEquals("12345", result.employeeID)
        assertEquals("https://example.com/johndoe.jpg", result.image)
    }

    @Test(expected = Exception::class)
    fun testServerErrorCausesException() = runTest {
        // Подготавливаем JSON-ответ с ошибкой и статусом 500
        val responseBody = """{ "error": "internal server error" }"""
        TestServerHolder.testMockServer.server.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setHeader("Content-Type", "application/json")
                .setBody(responseBody)
        )
        // Ожидаем, что use case выбросит исключение при ошибке сервера
        fetchConductorByTokenUseCase("dummyToken")
    }
}
