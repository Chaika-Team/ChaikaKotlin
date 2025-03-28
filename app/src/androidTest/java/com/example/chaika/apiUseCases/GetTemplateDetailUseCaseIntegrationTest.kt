package com.example.chaika.apiUseCases

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.chaika.domain.models.TemplateDomain
import com.example.chaika.domain.usecases.GetTemplateDetailUseCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import testUtils.TestMockServer
import testUtils.TestServerHolder
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class GetTemplateDetailUseCaseIntegrationTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    // Инжектируем use case для получения детальной информации о шаблоне
    @Inject
    lateinit var getTemplateDetailUseCase: GetTemplateDetailUseCase

    @Before
    fun setUp() {
        // Создаем новый экземпляр тестового сервера для этого класса
        val testServer = TestMockServer().apply { start() }
        // Записываем сервер в холдер, чтобы DI-модуль использовал его URL
        TestServerHolder.testMockServer = testServer

        hiltRule.inject()
    }

    @After
    fun tearDown() {
        // Завершаем работу тестового сервера для данного класса
        TestServerHolder.testMockServer.shutdown()
    }

    @Test
    fun testServerReturnsTemplateDetailForValidId() = runTest {
        // Подготавливаем JSON-ответ с детальной информацией о шаблоне, включая заполненный content
        val responseBody = """{
            "Template": {
                "id": 1,
                "template_name": "Template1 for Test",
                "description": "desc1",
                "content": [
                    { "product_id": 101, "quantity": 2 },
                    { "product_id": 102, "quantity": 3 }
                ]
            }
        }"""
        TestServerHolder.testMockServer.server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(responseBody)
        )

        val result: TemplateDomain = getTemplateDetailUseCase(1)
        // Проверяем ключевые свойства возвращаемого шаблона:
        // 1. ID шаблона
        assertEquals("Template ID должен быть 1", 1, result.id)
        // 2. Название шаблона
        assertEquals("Название шаблона должно совпадать", "Template1 for Test", result.templateName)
        // 3. Количество элементов в content
        assertEquals("Ожидается 2 элемента content", 2, result.content.size)
    }

    @Test(expected = Exception::class)
    fun testServerErrorCausesException() = runTest {
        // Эмулируем серверную ошибку с кодом 500
        TestServerHolder.testMockServer.server.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setHeader("Content-Type", "application/json")
                .setBody("")
        )
        // Ожидается, что вызов getTemplateDetailUseCase выбросит исключение
        getTemplateDetailUseCase(1)
    }

    @Test
    fun testServerReturnsErrorMessageForNonExistingId() = runTest {
        TestServerHolder.testMockServer.server.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setHeader("Content-Type", "application/json")
                .setBody("""{ "error": "Template with ID -1 not found" }""")
        )
        try {
            getTemplateDetailUseCase(-1)
            fail("Expected an Exception to be thrown")
        } catch (e: Exception) {
            val message = e.message ?: ""
            // Проверяем, что сообщение исключения содержит префикс и код 404
            assertTrue(
                "Exception message should contain 'Error fetching template detail:'",
                message.contains("Error fetching template detail:")
            )
            assertTrue(
                "Exception message should contain '404'",
                message.contains("404")
            )
        }
    }

}
