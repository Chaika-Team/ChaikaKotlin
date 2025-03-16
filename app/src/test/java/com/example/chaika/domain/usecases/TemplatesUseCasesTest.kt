@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.chaika.domain.usecases

import com.example.chaika.data.dataSource.dto.TemplateContentDto
import com.example.chaika.data.dataSource.dto.TemplateDto
import com.example.chaika.data.dataSource.dto.TemplateListResponseDto
import com.example.chaika.domain.models.TemplateContentDomain
import com.example.chaika.domain.models.TemplateDomain
import com.example.chaika.data.dataSource.repo.ChaikaSoftApiServiceRepositoryInterface
import com.example.chaika.data.dataSource.mappers.toDomainList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.mockito.junit.jupiter.MockitoExtension

/**
 * Техника тест-дизайна: #1 Классы эквивалентности
 *
 * Автор: OwletsFox
 *
 * Описание:
 *  - Тест для FetchTemplatesUseCase.
 *  - Проверяется, что при успешном запросе к репозиторию use case возвращает ожидаемый список шаблонов.
 */
@ExtendWith(MockitoExtension::class)
class FetchTemplatesUseCaseTest {

    private val repository: ChaikaSoftApiServiceRepositoryInterface = mock()
    private val fetchTemplatesUseCase = FetchTemplatesUseCase(repository)

    @Test
    fun `invoke returns list of templates when repository succeeds`() = runTest {
        // Arrange: создаем dummy-список шаблонов
        val dummyTemplates = listOf(
            TemplateDomain(
                id = 1,
                templateName = "Template 1",
                description = "Description 1",
                content = listOf(TemplateContentDomain(productId = 1, quantity = 10))
            ),
            TemplateDomain(
                id = 2,
                templateName = "Template 2",
                description = "Description 2",
                content = listOf(TemplateContentDomain(productId = 2, quantity = 5))
            )
        )
        whenever(repository.fetchTemplates(limit = 100, offset = 0)).thenReturn(dummyTemplates)

        // Act: вызываем use case
        val result = fetchTemplatesUseCase.invoke()

        // Assert: результат должен совпадать с dummyTemplates
        assertEquals(dummyTemplates, result)
    }

    /**
     * Техника тест-дизайна: #2 Граничные условия
     *
     * Автор: OwletsFox
     *
     * Описание:
     *  - Тест для FetchTemplatesUseCase.
     *  - Проверяется, что если репозиторий выбрасывает исключение, use case пробрасывает это исключение.
     */
    @Test
    fun `invoke throws exception when repository fails`() = runTest {
        // Arrange: настраиваем репозиторий так, чтобы он выбрасывал исключение
        val errorMessage = "Error fetching templates"
        whenever(repository.fetchTemplates(limit = 100, offset = 0))
            .thenThrow(RuntimeException(errorMessage))

        // Act & Assert: проверяем, что use case выбрасывает ожидаемое исключение
        try {
            fetchTemplatesUseCase.invoke()
            fail("Expected exception was not thrown")
        } catch (e: Exception) {
            assertEquals(errorMessage, e.message)
        }
    }
}
