package com.example.chaika.templates

import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltAndroidRule
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import javax.inject.Inject
import com.example.chaika.data.dataSource.repo.ChaikaSoftApiServiceRepositoryInterface
import com.example.chaika.domain.models.TemplateDomain

@HiltAndroidTest
class TemplateDetailIntegrationTests {

    private val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: ChaikaSoftApiServiceRepositoryInterface

    @BeforeEach
    fun initHilt() {
        hiltRule.inject()
    }

    @Test
    fun testSuccessfulTemplateDetailRetrieval() = runTest {
        val templates: List<TemplateDomain> = repository.fetchTemplates("", limit = 20, offset = 0)
        assertFalse(templates.isEmpty(), "Template list is empty, cannot test detail retrieval")
        val validId = templates.first().id

        val detail: TemplateDomain = repository.fetchTemplateDetail(validId)
        assertNotNull(detail, "Template detail should not be null for valid id: $validId")
        // Если ожидается заполненный content, можно добавить проверку:
        // assertFalse(detail.content.isEmpty(), "Expected non-empty content for template with id: $validId")
    }

    @Test
    fun testTemplateDetailNotFoundThrowsException() = runTest {
        assertThrows<Exception> {
            repository.fetchTemplateDetail(-1)
        }
    }

    @Test
    fun testTemplateDetailWhenServiceUnavailableThrowsException() = runTest {
        assertThrows<Exception> {
            repository.fetchTemplateDetail(999999)
        }
    }
}
