package com.chaikasoft.app.apiUseCases

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.chaikasoft.app.domain.common.AppError
import com.chaikasoft.app.domain.common.RemoteResult
import com.chaikasoft.app.domain.models.TemplateDomain
import com.chaikasoft.app.domain.usecases.GetTemplateDetailUseCase
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
class GetTemplateDetailUseCaseIntegrationTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var getTemplateDetailUseCase: GetTemplateDetailUseCase

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
    fun testServerReturnsTemplateDetailForValidId() = runTest {
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

        val result: TemplateDomain = getTemplateDetailUseCase(1).successOrFail()

        assertEquals("Template ID should be 1", 1, result.id)
        assertEquals("Template name should match", "Template1 for Test", result.templateName)
        assertEquals("Expected 2 content items", 2, result.content.size)
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

        val result = getTemplateDetailUseCase(1)

        assertTrue(result is RemoteResult.Failure)
        assertEquals(500, ((result as RemoteResult.Failure).error as AppError.Http).code)
    }

    @Test
    fun testServerReturnsFailureForNonExistingId() = runTest {
        TestServerHolder.testMockServer.server.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setHeader("Content-Type", "application/json")
                .setBody("""{ "error": "Template with ID -1 not found" }""")
        )

        val result = getTemplateDetailUseCase(-1)

        assertTrue(result is RemoteResult.Failure)
        val error = (result as RemoteResult.Failure).error as AppError.Http
        assertEquals(404, error.code)
        assertTrue(error.body.orEmpty().contains("Template with ID -1 not found"))
    }

    private fun <T> RemoteResult<T>.successOrFail(): T =
        when (this) {
            is RemoteResult.Success -> data
            is RemoteResult.Failure -> throw AssertionError("Expected success, got $error")
        }
}
