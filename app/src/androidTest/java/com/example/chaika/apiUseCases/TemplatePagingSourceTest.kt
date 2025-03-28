package com.example.chaika.apiUseCases

import androidx.paging.PagingSource
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.chaika.data.dataSource.repo.ChaikaSoftApiServiceRepositoryInterface
import com.example.chaika.data.dataSource.repo.TemplatePagingSource
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import testUtils.TestMockServer
import testUtils.TestServerHolder
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class TemplatePagingSourceTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    // Инжектируем репозиторий, который используется TemplatePagingSource
    @Inject
    lateinit var repository: ChaikaSoftApiServiceRepositoryInterface

    // Будем использовать TestMockServer через TestServerHolder
    private lateinit var testMockServer: TestMockServer

    @Before
    fun setUp() {
        // Создаем новый экземпляр TestMockServer для этого класса
        testMockServer = TestMockServer().apply { start() }
        // Записываем его в TestServerHolder, чтобы AndroidTestApiModule мог получить актуальный базовый URL
        TestServerHolder.testMockServer = testMockServer

        hiltRule.inject()
    }

    @After
    fun tearDown() {
        // Останавливаем сервер после завершения тестов данного класса
        testMockServer.shutdown()
    }

    @Test
    fun testTemplatePagingSourceDirectly() = runTest {
        // Подготавливаем корректный JSON-ответ с двумя шаблонами
        val responseBody = """{
            "Templates": [
                { "id": 1, "template_name": "Template1 for Test", "description": "desc1", "content": [] },
                { "id": 2, "template_name": "Template2 for Test", "description": "desc2", "content": [] }
            ]
        }"""
        // Enqueue ответ от нового экземпляра сервера
        testMockServer.server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(responseBody)
        )

        // Создаем PagingSource напрямую с пустым query
        val pagingSource = TemplatePagingSource(repository = repository, query = "")

        // Вызываем load() с параметрами Refresh
        val loadResult = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        when (loadResult) {
            is PagingSource.LoadResult.Page -> {
                // Ожидаем, что будет возвращено 2 шаблона
                assertEquals("Expected 2 templates", 2, loadResult.data.size)
            }
            is PagingSource.LoadResult.Error -> {
                fail("LoadResult.Error: ${loadResult.throwable}")
            }
            else -> fail("Unexpected LoadResult: $loadResult")
        }
    }
}
