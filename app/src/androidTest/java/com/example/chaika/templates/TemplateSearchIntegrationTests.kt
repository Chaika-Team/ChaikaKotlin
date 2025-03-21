package com.example.chaika.templates

import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import kotlinx.coroutines.test.runTest
import javax.inject.Inject
import com.example.chaika.data.dataSource.repo.ChaikaSoftApiServiceRepositoryInterface
import com.example.chaika.domain.models.TemplateDomain

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class TemplateSearchIntegrationTests {

    @Inject
    lateinit var repository: ChaikaSoftApiServiceRepositoryInterface

    // Вызовите HiltAndroidRule.inject() через JUnit4 правило (если необходимо)
    // Например, если вы используете HiltAndroidRule:
    // @get:Rule
    // var hiltRule = HiltAndroidRule(this)

    @Test
    fun testSuccessfulTemplateSearchWithValidQuery() = runTest {
        val query = "Kotlin"
        val templates: List<TemplateDomain> =
            repository.fetchTemplates(query, limit = 20, offset = 0)
        assertFalse(templates.isEmpty(), "Expected non-empty template list for query: $query")
    }

    @Test
    fun testSuccessfulTemplateSearchWithEmptyQueryReturnsAllTemplates() = runTest {
        val query = ""
        val templates: List<TemplateDomain> =
            repository.fetchTemplates(query, limit = 20, offset = 0)
        assertFalse(templates.isEmpty(), "Expected non-empty template list for empty query")
    }

    @Test
    fun testEmptyTemplateSearchReturnsEmptyList() = runTest {
        val query = "asldfjalskdfj"
        val templates: List<TemplateDomain> =
            repository.fetchTemplates(query, limit = 20, offset = 0)
        assertTrue(templates.isEmpty(), "Expected empty template list for query: $query")
    }

    @Test
    fun testTemplateSearchWhenServiceUnavailableThrowsException() = runTest {
        assertThrows<Exception> {
            repository.fetchTemplates("trigger_service_down", limit = 20, offset = 0)
        }
    }
}
