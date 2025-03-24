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
class TemplateDetailIntegrationTests {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: ChaikaSoftApiServiceRepositoryInterface

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun testSuccessfulTemplateDetailRetrieval() = runBlocking {
        // Получаем список шаблонов (где content изначально пустой)
        val templates: List<TemplateDomain> = repository.fetchTemplates("", limit = 20, offset = 0)
        assertFalse("Template list is empty, cannot test detail retrieval", templates.isEmpty())
        val validId = templates.first().id

        // Запрашиваем детальную информацию по шаблону
        val detail: TemplateDomain = repository.fetchTemplateDetail(validId)
        assertNotNull("Template detail should not be null for valid id: $validId", detail)
        // Если ожидается заполненный content, можно добавить проверку:
        // assertFalse("Expected non-empty content for template with id: $validId", detail.content.isEmpty())
    }

    @Test
    fun testTemplateDetailNotFoundThrowsException() = runBlocking {
        try {
            repository.fetchTemplateDetail(-1)
            fail("Expected an exception for an invalid template id")
        } catch (e: Exception) {
            // Исключение ожидается – тест проходит
        }
    }

    @Test
    fun testTemplateDetailWhenServiceUnavailableThrowsException() = runBlocking {
        try {
            repository.fetchTemplateDetail(999999)
            fail("Expected an exception when the service is unavailable")
        } catch (e: Exception) {
            // Исключение ожидается – тест проходит
        }
    }
}
