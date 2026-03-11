package com.chaikasoft.app.domain.usecases.conductorTripShiftUseCases

import com.chaikasoft.app.data.dataSource.repo.ChaikaSoftReportsRepositoryInterface
import com.chaikasoft.app.domain.sealed.SendReportResult
import com.chaikasoft.app.domain.sealed.UploadResult
import com.chaikasoft.app.domain.usecases.UploadShiftReportUseCase
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.test.runTest


class UploadShiftReportUseCaseTest : FunSpec({

    lateinit var repo: ChaikaSoftReportsRepositoryInterface
    lateinit var useCase: UploadShiftReportUseCase

    val reportJson = """{"some":"report"}"""

    // Аналог @BeforeEach: выполняется перед каждым test(...)
    beforeTest {
        repo = mockk()
        useCase = UploadShiftReportUseCase(repo)
    }

    /**
     * Техника тест-дизайна: #7 Таблица решений (Decision Table)
     *
     * Автор: OwletsFox
     *
     * Описание:
     *   - Комбинация условий:
     *       repoResult = Ok (2xx)
     *   - Ожидаемое поведение:
     *       1) юзкейс возвращает Success,
     *       2) вызывает repo.uploadShiftReport(json) ровно один раз.
     *   - Цель: зафиксировать, что успешный ответ сервера маппится в Success без дополнительных условий.
     */
    test("when repo returns Ok - maps to Success") {
        runTest {
            coEvery { repo.uploadShiftReport(reportJson) } returns UploadResult.Ok

            val result = useCase(reportJson)

            result shouldBe SendReportResult.Success

            coVerify(exactly = 1) { repo.uploadShiftReport(reportJson) }
            confirmVerified(repo)
        }
    }

    /**
     * Техника тест-дизайна: #2 Граничные значения
     *
     * Автор: OwletsFox
     *
     * Описание:
     *   - Граница по HTTP-коду: 409 (конфликт) интерпретируется как идемпотентный успех.
     *   - Ожидаемое поведение:
     *       1) юзкейс возвращает Success,
     *       2) никаких дополнительных эффектов кроме вызова репозитория.
     *   - Цель: защитить инвариант идемпотентности "повторная отправка не считается ошибкой".
     */
    test("when repo returns HttpError 409 - maps to Success (idempotent)") {
        runTest {
            coEvery { repo.uploadShiftReport(reportJson) } returns UploadResult.HttpError(
                code = 409,
                body = "conflict"
            )

            val result = useCase(reportJson)

            result shouldBe SendReportResult.Success

            coVerify(exactly = 1) { repo.uploadShiftReport(reportJson) }
            confirmVerified(repo)
        }
    }

    /**
     * Техника тест-дизайна: #7 Таблица решений (Decision Table)
     *
     * Автор: OwletsFox
     *
     * Описание:
     *   - Комбинация условий:
     *       repoResult = HttpError (4xx, кроме 409)
     *   - Ожидаемое поведение:
     *       1) юзкейс возвращает PermanentFailure с тем же кодом и body,
     *       2) не делает повторных попыток.
     *   - Цель: зафиксировать, что клиентские ошибки не должны ретраиться.
     */
    test("when repo returns HttpError 400 - maps to PermanentFailure with same details") {
        runTest {
            val body = "bad request"
            coEvery { repo.uploadShiftReport(reportJson) } returns UploadResult.HttpError(
                code = 400,
                body = body
            )

            val result = useCase(reportJson)

            result shouldBe SendReportResult.PermanentFailure(httpCode = 400, serverBody = body)

            coVerify(exactly = 1) { repo.uploadShiftReport(reportJson) }
            confirmVerified(repo)
        }
    }

    /**
     * Техника тест-дизайна: #7 Таблица решений (Decision Table)
     *
     * Автор: OwletsFox
     *
     * Описание:
     *   - Комбинация условий:
     *       repoResult = HttpError (5xx)
     *   - Ожидаемое поведение:
     *       1) юзкейс возвращает TemporaryFailure с httpCode,
     *       2) позволяет безопасный повтор позже.
     *   - Цель: защитить правило "серверные ошибки => временный сбой".
     */
    test("when repo returns HttpError 503 - maps to TemporaryFailure with httpCode") {
        runTest {
            coEvery { repo.uploadShiftReport(reportJson) } returns UploadResult.HttpError(
                code = 503,
                body = "server error"
            )

            val result = useCase(reportJson)

            result shouldBe SendReportResult.TemporaryFailure(httpCode = 503)

            coVerify(exactly = 1) { repo.uploadShiftReport(reportJson) }
            confirmVerified(repo)
        }
    }

    /**
     * Техника тест-дизайна: #3 Диаграмма переходов состояний
     *
     * Автор: OwletsFox
     *
     * Описание:
     *   - Событие: сетевой сбой при отправке (NetworkError).
     *   - Ожидаемое поведение:
     *       1) юзкейс возвращает TemporaryFailure(isNetwork = true),
     *       2) ошибка считается временной и может быть ретраена.
     *   - Цель: зафиксировать, что сетевые сбои не переводят сценарий в "PermanentFailure".
     */
    test("when repo returns NetworkError - maps to TemporaryFailure with isNetwork") {
        runTest {
            coEvery { repo.uploadShiftReport(reportJson) } returns UploadResult.NetworkError(
                throwable = RuntimeException("boom")
            )

            val result = useCase(reportJson)

            result shouldBe SendReportResult.TemporaryFailure(isNetwork = true)

            coVerify(exactly = 1) { repo.uploadShiftReport(reportJson) }
            confirmVerified(repo)
        }
    }
})
