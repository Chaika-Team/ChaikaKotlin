package com.chaikasoft.app.apiUseCases

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.chaikasoft.app.domain.common.AppError
import com.chaikasoft.app.domain.common.RemoteResult
import com.chaikasoft.app.domain.models.TemplateDomain
import com.chaikasoft.app.domain.usecases.GetTemplatesUseCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject
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

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class GetTemplatesUseCaseIntegrationTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var getTemplatesUseCase: GetTemplatesUseCase

    @Before
    fun setUp() {
        val testServer = TestMockServer().apply { start() }
        TestServerHolder.testMockServer = testServer

        hiltRule.inject()
    }

    @After
    fun tearDown() {
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

        val result: List<TemplateDomain> = getTemplatesUseCase(
            query = "Test",
            limit = 100,
            offset = 0
        ).successOrFail()

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

        val result: List<TemplateDomain> = getTemplatesUseCase(
            query = "non_existing_query",
            limit = 100,
            offset = 0
        ).successOrFail()

        assertTrue("Expected empty template list for query 'non_existing_query'", result.isEmpty())
    }

    @Test
    fun testServerErrorReturnsFailure() = runTest {
        val responseBody = """{
             "error": "internal server error"
        }"""
        TestServerHolder.testMockServer.server.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setHeader("Content-Type", "application/json")
                .setBody(responseBody)
        )

        val result = getTemplatesUseCase(query = "", limit = 100, offset = 0)

        assertTrue(result is RemoteResult.Failure)
        assertEquals(500, ((result as RemoteResult.Failure).error as AppError.Http).code)
    }

    private fun <T> RemoteResult<T>.successOrFail(): T =
        when (this) {
            is RemoteResult.Success -> data
            is RemoteResult.Failure -> throw AssertionError("Expected success, got $error")
        }
}
