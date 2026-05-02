package com.chaikasoft.app.data.datasource.repo

import com.chaikasoft.app.data.datasource.apiservice.ChaikaSoftApiService
import com.chaikasoft.app.domain.sealed.UploadResult
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import java.io.IOException
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import retrofit2.HttpException
import retrofit2.Response

class ChaikaSoftReportsRepositoryTest : FunSpec({

    lateinit var api: ChaikaSoftApiService
    lateinit var repository: ChaikaSoftReportsRepository

    beforeTest {
        api = mockk()
        repository = ChaikaSoftReportsRepository(api)
    }

    /**
     * Техника тест-дизайна: #3 Таблица решений
     *
     * Описание:
     *   - Комбинация: POST report завершился 2xx.
     *   - Ожидаемое поведение: UploadResult.Ok и корректный JSON content-type/body.
     *   - Цель: зафиксировать happy-path отправки отчета и формат тела запроса.
     */
    test("uploadShiftReport success returns Ok and sends json request body") {
        runTest {
            val bodySlot = slot<RequestBody>()
            coEvery { api.sendShiftReport(capture(bodySlot)) } returns Unit
            val json = """{"shift":"ok"}"""

            val result = repository.uploadShiftReport(json)

            result shouldBe UploadResult.Ok
            bodySlot.captured.contentType()?.toString() shouldBe "application/json; charset=utf-8"
            val buffer = Buffer()
            bodySlot.captured.writeTo(buffer)
            buffer.readUtf8() shouldBe json
            coVerify(exactly = 1) { api.sendShiftReport(any()) }
        }
    }

    /**
     * Техника тест-дизайна: #3 Таблица решений
     *
     * Описание:
     *   - Комбинация: API вернул HttpException.
     *   - Ожидаемое поведение: UploadResult.HttpError(code, body).
     *   - Цель: защитить маппинг серверных ошибок отправки отчета.
     */
    test("uploadShiftReport http error returns HttpError with code and body") {
        runTest {
            val http = HttpException(
                Response.error<Unit>(
                    409,
                    "conflict".toResponseBody("text/plain".toMediaType()),
                ),
            )
            coEvery { api.sendShiftReport(any()) } throws http

            val result = repository.uploadShiftReport("""{"shift":"x"}""")

            result shouldBe UploadResult.HttpError(code = 409, body = "conflict")
        }
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Описание:
     *   - Эквивалентный класс: IOException на транспортном уровне.
     *   - Ожидаемое поведение: UploadResult.NetworkError с исходным throwable.
     *   - Цель: закрепить сетевой класс ошибок.
     */
    test("uploadShiftReport io error returns NetworkError with original throwable") {
        runTest {
            val error = IOException("network")
            coEvery { api.sendShiftReport(any()) } throws error

            val result = repository.uploadShiftReport("""{"shift":"x"}""")

            (result as UploadResult.NetworkError).throwable shouldBe error
        }
    }

    /**
     * Техника тест-дизайна: #5 Error Guessing
     *
     * Описание:
     *   - Сценарий: непредвиденное исключение в процессе отправки.
     *   - Ожидаемое поведение: UploadResult.NetworkError.
     *   - Цель: зафиксировать fallback-ветку, чтобы не терять ошибку на верхних слоях.
     */
    test("uploadShiftReport unexpected error returns NetworkError with original throwable") {
        runTest {
            val error = IllegalStateException("boom")
            coEvery { api.sendShiftReport(any()) } throws error

            val result = repository.uploadShiftReport("""{"shift":"x"}""")

            (result as UploadResult.NetworkError).throwable shouldBe error
        }
    }
})
