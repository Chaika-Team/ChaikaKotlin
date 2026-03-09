package com.chaikasoft.app.domain.usecases.conductorTripShiftUseCases

import com.chaikasoft.app.domain.models.trip.TripShiftStatusDomain
import com.chaikasoft.app.domain.sealed.SendReportResult
import com.chaikasoft.app.domain.usecases.GetShiftReportJsonUseCase
import com.chaikasoft.app.domain.usecases.MarkShiftSentUseCase
import com.chaikasoft.app.domain.usecases.SendShiftReportUseCase
import com.chaikasoft.app.domain.usecases.UploadShiftReportUseCase
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.test.runTest


class SendShiftReportUseCaseTest : FunSpec({

    lateinit var getReport: GetShiftReportJsonUseCase
    lateinit var upload: UploadShiftReportUseCase
    lateinit var markSent: MarkShiftSentUseCase
    lateinit var useCase: SendShiftReportUseCase

    val uuid = "trip-uuid-123"
    val reportJson = """{"some":"report"}"""

    // Аналог @BeforeEach: выполняется перед каждым test(...)
    beforeTest {
        getReport = mockk()
        upload = mockk()
        markSent = mockk()
        useCase = SendShiftReportUseCase(getReport, upload, markSent)
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: OwletsFox
     *
     * Описание:
     *   - Эквивалентный класс входных данных: смена уже в статусе SENT.
     *   - Ожидаемое поведение:
     *       1) юзкейс возвращает AlreadySent,
     *       2) не выполняет загрузку отчёта (upload не вызывается),
     *       3) не выполняет побочный эффект "пометить SENT" (markSent не вызывается).
     *   - Цель: зафиксировать идемпотентность и запрет повторной отправки уже отправленной смены.
     */
    test("when status is SENT - returns AlreadySent and does not upload") {
        runTest {
            coEvery { getReport(uuid) } returns (TripShiftStatusDomain.SENT to reportJson)

            val result = useCase(uuid)

            result shouldBe SendReportResult.AlreadySent

            coVerify(exactly = 1) { getReport(uuid) }
            coVerify(exactly = 0) { upload(any()) }
            coVerify(exactly = 0) { markSent(any()) }
            confirmVerified(getReport, upload, markSent)
        }
    }

    /**
     * Техника тест-дизайна: #2 Граничные значения
     *
     * Автор: OwletsFox
     *
     * Описание:
     *   - Граница по данным отчёта: reportJson отсутствует (null/blank) => отправлять нечего.
     *   - Ожидаемое поведение:
     *       1) юзкейс возвращает MissingReport,
     *       2) не выполняет сетевой вызов upload,
     *       3) не меняет состояние смены (markSent не вызывается).
     *   - Цель: защититься от отправки "пустого" отчёта и лишних внешних действий.
     */
    test("when report is missing - returns MissingReport and does not upload") {
        runTest {
            coEvery { getReport(uuid) } returns (TripShiftStatusDomain.FINISHED to null)

            val result = useCase(uuid)

            result shouldBe SendReportResult.MissingReport

            coVerify(exactly = 1) { getReport(uuid) }
            coVerify(exactly = 0) { upload(any()) }
            coVerify(exactly = 0) { markSent(any()) }
            confirmVerified(getReport, upload, markSent)
        }
    }

    /**
     * Техника тест-дизайна: #7 Таблица решений (Decision Table)
     *
     * Автор: OwletsFox
     *
     * Описание:
     *   - Комбинация условий (строка таблицы решений):
     *       status = FINISHED, reportJson = present, uploadResult = Success
     *   - Ожидаемое поведение:
     *       1) юзкейс возвращает Success,
     *       2) вызывает upload(reportJson),
     *       3) выполняет переход состояния: помечает смену как SENT (markSent),
     *       4) порядок важен: markSent выполняется только после успешного upload.
     *   - Цель: зафиксировать корректный переход FINISHED -> SENT и предотвратить "ложное SENT".
     */
    test("when upload succeeds - marks shift as SENT and returns Success") {
        runTest {
            coEvery { getReport(uuid) } returns (TripShiftStatusDomain.FINISHED to reportJson)
            coEvery { upload(reportJson) } returns SendReportResult.Success
            coEvery { markSent(uuid) } returns Unit

            val result = useCase(uuid)

            result shouldBe SendReportResult.Success

            // Порядок важен: SENT только после успешной отправки
            coVerifyOrder {
                getReport(uuid)
                upload(reportJson)
                markSent(uuid)
            }
            confirmVerified(getReport, upload, markSent)
        }
    }

    /**
     * Техника тест-дизайна: #7 Таблица решений (Decision Table)
     *
     * Автор: OwletsFox
     *
     * Описание:
     *   - Комбинация условий (строка таблицы решений):
     *       status = FINISHED, reportJson = present, uploadResult = TemporaryFailure
     *   - Ожидаемое поведение:
     *       1) юзкейс возвращает TemporaryFailure (без изменения),
     *       2) вызывает upload(reportJson),
     *       3) НЕ выполняет переход в SENT (markSent не вызывается).
     *   - Цель: зафиксировать, что при временной ошибке система остаётся в состоянии "можно повторить".
     */
    test("when upload is TemporaryFailure - does not mark SENT and returns the same failure") {
        runTest {
            coEvery { getReport(uuid) } returns (TripShiftStatusDomain.FINISHED to reportJson)
            val failure = SendReportResult.TemporaryFailure(isNetwork = true)
            coEvery { upload(reportJson) } returns failure

            val result = useCase(uuid)

            result shouldBe failure

            coVerify(exactly = 1) { getReport(uuid) }
            coVerify(exactly = 1) { upload(reportJson) }
            coVerify(exactly = 0) { markSent(any()) }
            confirmVerified(getReport, upload, markSent)
        }
    }

    /**
     * Техника тест-дизайна: #7 Таблица решений (Decision Table)
     *
     * Автор: OwletsFox
     *
     * Описание:
     *   - Комбинация условий (строка таблицы решений):
     *       status = FINISHED, reportJson = present, uploadResult = PermanentFailure
     *   - Ожидаемое поведение:
     *       1) юзкейс возвращает PermanentFailure (без изменения),
     *       2) вызывает upload(reportJson),
     *       3) НЕ выполняет переход в SENT (markSent не вызывается).
     *   - Цель: зафиксировать, что при "неисправимой" ошибке статус не меняется,
     *           и повторная отправка без изменения входных данных не поможет.
     */
    test("when upload is PermanentFailure - does not mark SENT and returns the same failure") {
        runTest {
            coEvery { getReport(uuid) } returns (TripShiftStatusDomain.FINISHED to reportJson)
            val failure = SendReportResult.PermanentFailure(httpCode = 400, serverBody = "bad request")
            coEvery { upload(reportJson) } returns failure

            val result = useCase(uuid)

            result shouldBe failure

            coVerify(exactly = 1) { getReport(uuid) }
            coVerify(exactly = 1) { upload(reportJson) }
            coVerify(exactly = 0) { markSent(any()) }
            confirmVerified(getReport, upload, markSent)
        }
    }
})