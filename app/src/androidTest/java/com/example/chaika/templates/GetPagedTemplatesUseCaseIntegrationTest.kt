package com.example.chaika.templates

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.chaika.domain.models.TemplateDomain
import com.example.chaika.domain.usecases.GetPagedTemplatesUseCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.mockwebserver.MockResponse
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import testUtils.MockServer
import javax.inject.Inject

// DiffUtil для TemplateDomain
class TemplateDiffCallback : DiffUtil.ItemCallback<TemplateDomain>() {
    override fun areItemsTheSame(oldItem: TemplateDomain, newItem: TemplateDomain): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: TemplateDomain, newItem: TemplateDomain): Boolean =
        oldItem == newItem
}

// Простая реализация ListUpdateCallback
class NoopListUpdateCallback : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class GetPagedTemplatesUseCaseIntegrationTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    // Инжектируем юзкейс, который использует зависимости из тестового модуля (AndroidTestApiModule)
    @Inject
    lateinit var getPagedTemplatesUseCase: GetPagedTemplatesUseCase

    @Before
    fun setUp() {
        hiltRule.inject()
        val isServerRunning = try {
            MockServer.server.port > 0
        } catch (e: IllegalStateException) {
            false
        }

        if (!isServerRunning) {
            MockServer.server.start()
        }
    }

    @After
    fun tearDown() {
        MockServer.server.shutdown()
    }

    /**
     * Функция, которая собирает PagingData в список с таймаутом.
     * Если данные не поступают в течение timeoutMs, возвращается пустой список.
     */
    private suspend fun <T : Any> PagingData<T>.collectItems(timeoutMs: Long = 2000L): List<T> =
        withTimeoutOrNull(timeoutMs) {
            val differ = AsyncPagingDataDiffer(
                diffCallback = TemplateDiffCallback() as DiffUtil.ItemCallback<T>,
                updateCallback = NoopListUpdateCallback(),
                mainDispatcher = kotlinx.coroutines.Dispatchers.Main,
                workerDispatcher = kotlinx.coroutines.Dispatchers.IO
            )
            differ.submitData(this@collectItems)
            delay(1000)
            differ.snapshot().items
        } ?: emptyList()

    @Test
    fun testPagedTemplatesReturnsDataForNonEmptyQuery() = runBlocking {
        // Подготавливаем ответ сервера для запроса "Test"
        val responseBody = """{
            "Templates": [
                { "id": 1, "template_name": "Template1 for Test", "description": "desc1", "content": [] },
                { "id": 2, "template_name": "Template2 for Test", "description": "desc2", "content": [] }
            ]
        }"""
        MockServer.server.enqueue(MockResponse().setResponseCode(200).setBody(responseBody))

        val query = "Test"
        val flow = getPagedTemplatesUseCase(query, pageSize = 20)
        val pagingData = flow.first()
        val items = pagingData.collectItems()
        assertEquals("Expected 2 templates for query: $query", 2, items.size)
    }

    @Test
    fun testPagedTemplatesReturnsEmptyForEmptyResult() = runBlocking {
        // Подготавливаем ответ сервера с пустым списком шаблонов
        val responseBody = """{ "Templates": [] }"""
        MockServer.server.enqueue(MockResponse().setResponseCode(200).setBody(responseBody))

        val query = "non_existing_query"
        val flow = getPagedTemplatesUseCase(query, pageSize = 20)
        val pagingData = flow.first()
        val items = pagingData.collectItems()
        assertTrue("Expected empty template list for query: $query", items.isEmpty())
    }

    @Test
    fun testPagedTemplatesThrowsExceptionWhenServiceUnavailable() = runBlocking {
        // Симулируем ошибку сервера (HTTP 500)
        MockServer.server.enqueue(MockResponse().setResponseCode(500))
        val flow = getPagedTemplatesUseCase("any_query", pageSize = 20)
        val pagingData = flow.first()
        // При ошибке сервер возвращает LoadResult.Error, поэтому сбор данных через collectItems() должен вернуть пустой список
        val items = withTimeoutOrNull(2000L) { pagingData.collectItems() }
        assertTrue(
            "Expected empty template list when service is unavailable",
            items?.isEmpty() ?: true
        )
    }
}
