package com.chaikasoft.app.data.datasource.repo

import com.chaikasoft.app.data.datasource.apiservice.IAMApiService
import com.chaikasoft.app.data.datasource.dto.ConductorDto
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response

class IAMApiServiceRepositoryTest : FunSpec({

    lateinit var api: IAMApiService
    lateinit var repository: IAMApiServiceRepository

    beforeTest {
        api = mockk()
        repository = IAMApiServiceRepository(api)
    }

    /**
     * Техника тест-дизайна: #3 Таблица решений
     *
     * Описание:
     *   - Комбинация: успешный ответ IAM API.
     *   - Ожидаемое поведение: токен отправляется с префиксом Bearer, DTO маппится в Result.success.
     *   - Цель: защитить базовый контракт авторизационного запроса и успешного маппинга.
     */
    test("fetchUserInfo success returns mapped ConductorDomain and sends Bearer token") {
        runTest {
            val dto = ConductorDto(
                firstName = "Ivan",
                familyName = "Petrov",
                middleName = "Ivanovich",
                preferredUsername = "EMP-1",
                picture = "https://example.com/avatar.png",
            )
            coEvery { api.getUserInfo("Bearer token-123") } returns dto

            val result = repository.fetchUserInfo("token-123")

            result.getOrNull()?.name shouldBe "Ivan"
            result.getOrNull()?.employeeID shouldBe "EMP-1"
            result.isSuccess shouldBe true
            coVerify(exactly = 1) { api.getUserInfo("Bearer token-123") }
        }
    }

    /**
     * Техника тест-дизайна: #5 Error Guessing
     *
     * Описание:
     *   - Сценарий: IAM API возвращает HttpException.
     *   - Ожидаемое поведение: Result.failure с предсказуемым текстом "HTTP <code>: <message>".
     *   - Цель: зафиксировать регрессионно-опасный формат ошибки для верхних слоев.
     */
    test("fetchUserInfo http error returns failure with formatted message") {
        runTest {
            val http = HttpException(
                Response.error<ConductorDto>(
                    500,
                    "oops".toResponseBody("text/plain".toMediaType()),
                ),
            )
            coEvery { api.getUserInfo(any()) } throws http

            val result = repository.fetchUserInfo("token-123")

            result.isFailure shouldBe true
            result.exceptionOrNull()?.message shouldBe "HTTP 500: Response.error()"
        }
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Описание:
     *   - Эквивалентный класс: не-HTTP исключения.
     *   - Ожидаемое поведение: исходное исключение пробрасывается в Result.failure без замены.
     *   - Цель: зафиксировать fallback-ветку обработки ошибок.
     */
    test("fetchUserInfo unknown error returns original failure") {
        runTest {
            val error = IllegalStateException("boom")
            coEvery { api.getUserInfo(any()) } throws error

            val result = repository.fetchUserInfo("token-123")

            result.isFailure shouldBe true
            result.exceptionOrNull() shouldBe error
        }
    }
})
