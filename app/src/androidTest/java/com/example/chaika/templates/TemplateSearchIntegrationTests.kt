package com.example.chaika.templates

import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import com.example.chaika.data.dataSource.repo.ChaikaSoftApiServiceRepositoryInterface
import com.example.chaika.domain.models.TemplateDomain

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class TemplateSearchIntegrationTests {

    // Правило Hilt, которое выполняет инъекцию перед каждым тестом
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: ChaikaSoftApiServiceRepositoryInterface

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun testSuccessfulTemplateSearchWithValidQuery() = runBlocking {
        val query = "СЗ"
        val templates: List<TemplateDomain> =
            repository.fetchTemplates(query, limit = 20, offset = 0)
        assertFalse("Expected non-empty template list for query: $query", templates.isEmpty())
    }

    @Test
    fun testSuccessfulTemplateSearchWithEmptyQueryReturnsAllTemplates() = runBlocking {
        val query = ""
        val templates: List<TemplateDomain> =
            repository.fetchTemplates(query, limit = 20, offset = 0)
        assertFalse("Expected non-empty template list for empty query", templates.isEmpty())
    }

    @Test
    fun testEmptyTemplateSearchReturnsEmptyList() = runBlocking {
        val query = "asldfjalskdfj"
        val templates: List<TemplateDomain> =
            repository.fetchTemplates(query, limit = 20, offset = 0)
        assertTrue("Expected empty template list for query: $query", templates.isEmpty())
    }

    @Test
    fun testTemplateSearchWhenServiceUnavailableThrowsException() = runBlocking {
        try {
            repository.fetchTemplates("trigger_service_down", limit = 20, offset = 0)
            fail("Expected exception when service is unavailable")
        } catch (e: Exception) {
            // Исключение ожидается – тест проходит
        }
    }
}
