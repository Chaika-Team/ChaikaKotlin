package com.example.chaika.data.dataSource.repo

import com.example.chaika.data.dataSource.apiService.IAMApiService
import com.example.chaika.data.dataSource.dto.ConductorDto
import com.example.chaika.domain.models.ConductorDomain
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import retrofit2.Response

/**
 * Тесты для ApiServiceRepository.
 */
class IAMApiServiceRepositoryTest {
    // Мок ApiService
    private val IAMApiService: IAMApiService = org.mockito.kotlin.mock()
    private val repository = IAMApiServiceRepository(IAMApiService)

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для ApiServiceRepository.fetchUserInfo в позитивном сценарии.
     *   - Классы эквивалентности: успешный Response с непустым телом, корректное преобразование DTO в доменную модель.
     */
    @Test
    fun testFetchUserInfo_success() =
        runBlocking {
            // Arrange: создаем тестовый DTO
            val dto =
                ConductorDto(
                    name = "Bob",
                    familyName = "Builder",
                    givenName = "Bobby",
                    nickname = "EMP789",
                    image = "https://example.com/bob.png",
                )
            // Мок успешного ответа
            whenever(IAMApiService.getUserInfo(eq("Bearer valid_token"))).thenReturn(
                Response.success(
                    dto,
                ),
            )

            // Act
            val result = repository.fetchUserInfo("valid_token")

            // Assert: проверяем, что результат успешный и корректно маппится
            assertTrue(result.isSuccess)
            val domain: ConductorDomain = result.getOrNull()!!
            assertEquals(0, domain.id)
            assertEquals("Bob", domain.name)
            assertEquals("Builder", domain.familyName)
            assertEquals("Bobby", domain.givenName)
            assertEquals("EMP789", domain.employeeID)
            assertEquals("https://example.com/bob.png", domain.image)
        }

    /**
     * Техника тест-дизайна: #2 Граничные значения
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для ApiServiceRepository.fetchUserInfo: успешный Response, но body() возвращает null.
     *   - Граничные значения: если тело ответа пустое, должен вернуться Result.failure с соответствующим сообщением.
     */
    @Test
    fun testFetchUserInfo_emptyBody() =
        runBlocking {
            // Arrange: мок успешного ответа с null телом
            whenever(IAMApiService.getUserInfo(eq("Bearer token_empty"))).thenReturn(
                Response.success(
                    null,
                ),
            )

            // Act
            val result = repository.fetchUserInfo("token_empty")

            // Assert: результат должен быть failure с Exception("User info is empty")
            assertTrue(result.isFailure)
            val exception = result.exceptionOrNull()
            assertNotNull(exception)
            assertEquals("User info is empty", exception?.message)
        }

    /**
     * Техника тест-дизайна: #3 Причинно-следственный анализ
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для ApiServiceRepository.fetchUserInfo: неуспешный Response.
     *   - Причинно-следственный анализ: если API возвращает ошибку (например, 404 Not Found),
     *     должен вернуться Result.failure с соответствующим сообщением.
     */
    @Test
    fun testFetchUserInfo_errorResponse() =
        runBlocking {
            // Arrange: создаем Response с ошибочным кодом и сообщением
            val errorResponse =
                Response.error<ConductorDto>(
                    404,
                    "{\"error\": \"Not Found\"}".toResponseBody("application/json".toMediaType()),
                )
            whenever(IAMApiService.getUserInfo(eq("Bearer invalid_token"))).thenReturn(
                errorResponse,
            )

            // Act
            val result = repository.fetchUserInfo("invalid_token")

            // Assert: проверяем, что результат failure и содержит сообщение об ошибке
            assertTrue(result.isFailure)
            val exception = result.exceptionOrNull()
            assertNotNull(exception)
            assertEquals("Error: 404 - " + errorResponse.message(), exception?.message)
        }

    /**
     * Техника тест-дизайна: #4 Прогнозирование ошибок
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для ApiServiceRepository.fetchUserInfo: сценарий, когда вызов API выбрасывает непроверяемое исключение (RuntimeException).
     *   - Прогнозирование ошибок: если apiService.getUserInfo выбрасывает RuntimeException (например, Network error),
     *     метод должен вернуть Result.failure с этим исключением.
     */
    @Test
    fun testFetchUserInfo_exceptionThrown() =
        runBlocking {
            // Arrange: Мокируем исключение при вызове API. Используем RuntimeException вместо Exception.
            val exceptionToThrow = RuntimeException("Network error")
            whenever(IAMApiService.getUserInfo(eq("Bearer exception_token"))).thenThrow(
                exceptionToThrow,
            )

            // Act: вызываем метод fetchUserInfo
            val result = repository.fetchUserInfo("exception_token")

            // Assert: результат должен быть failure, и возвращенное исключение должно совпадать с exceptionToThrow
            assertTrue(result.isFailure)
            assertEquals(exceptionToThrow, result.exceptionOrNull())
        }
}
