package com.example.chaika.templates

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.chaika.domain.models.TemplateDomain
import com.example.chaika.domain.usecases.GetTemplatesUseCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import testUtils.TestMockServer
import testUtils.TestServerHolder
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
        // Создаем новый экземпляр сервера для этого класса
        val testServer = TestMockServer().apply { start() }
        // Записываем его в холдер, чтобы DI-модуль использовал его URL
        TestServerHolder.testMockServer = testServer

        hiltRule.inject()
    }

    @After
    fun tearDown() {
        // Завершаем работу сервера для данного класса
        TestServerHolder.testMockServer.shutdown()
    }

    @Test
    fun testServerReturnsAllTemplatesForValidQuery() = runTest {
        val responseBody = """{
            "Templates": [
                { "id": 1, "template_name": "Template1 for Test", "description": "desc1", "content": [] },
                { "id": 2, "template_name": "Template2 for Test", "description": "desc2", "content": [] }
            ]
        }"""
        TestServerHolder.testMockServer.server.enqueue(
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
        TestServerHolder.testMockServer.server.enqueue(
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
        TestServerHolder.testMockServer.server.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setHeader("Content-Type", "application/json")
                .setBody("")
        )
        getTemplatesUseCase(query = "", limit = 100, offset = 0)
    }
}
