package com.example.chaika.templates

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.chaika.domain.models.TemplateDomain
import com.example.chaika.domain.usecases.GetTemplatesUseCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.QueueDispatcher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import testUtils.MockServer
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class GetTemplatesUseCaseIntegrationTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    // Инжектируем use case
    @Inject
    lateinit var getTemplatesUseCase: GetTemplatesUseCase

    @Before
    fun setUp() {
        // Если сервер не запущен, запускаем его
        val isServerRunning = try {
            MockServer.server.port > 0
        } catch (e: IllegalStateException) {
            false
        }
        if (!isServerRunning) {
            MockServer.server.start()
        } else {
            // Если сервер уже запущен, сбрасываем очередь ответов, установив стандартный QueueDispatcher
            MockServer.server.dispatcher = QueueDispatcher()
        }
        hiltRule.inject()
    }

    @After
    fun tearDown() {
        // Не останавливаем сервер здесь, чтобы не мешать другим тестам.
        // Если необходимо, остановку можно выполнить глобально после всех тестов.
    }

    @Test
    fun testServerReturnsAllTemplatesForValidQuery() = runTest {
        val responseBody = """{
            "Templates": [
                { "id": 1, "template_name": "Template1 for Test", "description": "desc1", "content": [] },
                { "id": 2, "template_name": "Template2 for Test", "description": "desc2", "content": [] }
            ]
        }"""
        MockServer.server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(responseBody)
        )

        val result: List<TemplateDomain> =
            getTemplatesUseCase(query = "Test", limit = 100, offset = 0)
        assertEquals("Expected 2 templates for query 'Test'", 2, result.size)
    }

    @Test
    fun testServerReturnsEmptyListForNonMatchingQuery() = runTest {
        val responseBody = """{ "Templates": [] }"""
        MockServer.server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(responseBody)
        )

        val result: List<TemplateDomain> =
            getTemplatesUseCase(query = "non_existing_query", limit = 100, offset = 0)
        assertTrue("Expected empty template list for query 'non_existing_query'", result.isEmpty())
    }

    @Test(expected = Exception::class)
    fun testServerErrorCausesException() = runTest {
        MockServer.server.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setHeader("Content-Type", "application/json")
                .setBody("")
        )
        getTemplatesUseCase(query = "", limit = 100, offset = 0)
    }
}
