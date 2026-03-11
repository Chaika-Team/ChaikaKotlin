package com.chaikasoft.app.domain.usecases.conductorTripShiftUseCases

import com.chaikasoft.app.domain.sealed.SendReportResult
import com.chaikasoft.app.domain.usecases.CompleteShiftAndSendUseCase
import com.chaikasoft.app.domain.usecases.GenerateShiftReportUseCase
import com.chaikasoft.app.domain.usecases.SendShiftReportUseCase
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.test.runTest


class CompleteShiftAndSendUseCaseTest : FunSpec({

    lateinit var generate: GenerateShiftReportUseCase
    lateinit var send: SendShiftReportUseCase
    lateinit var useCase: CompleteShiftAndSendUseCase

    val uuid = "trip-uuid-123"
    val reportJson = """{"some":"report"}"""

    // Аналог @BeforeEach: выполняется перед каждым test(...)
    beforeTest {
        generate = mockk()
        send = mockk()
        useCase = CompleteShiftAndSendUseCase(generate, send)
    }

    /**
     * Техника тест-дизайна: #7 Таблица решений (Decision Table)
     *
     * Автор: OwletsFox
     *
     * Описание:
     *   - Комбинация условий:
     *       generate = ok, send = Success
     *   - Ожидаемое поведение:
     *       1) юзкейс возвращает Success,
     *       2) вызывает generate до send (порядок важен),
     *       3) выполняет ровно одну отправку.
     *   - Цель: зафиксировать бизнес-инвариант "сначала сформировать отчёт, потом отправить".
     */
    test("when generate succeeds and send returns Success - returns Success and calls in order") {
        runTest {
            coEvery { generate(uuid) } returns reportJson
            coEvery { send(uuid) } returns SendReportResult.Success

            val result = useCase(uuid)

            result shouldBe SendReportResult.Success

            // Порядок важен: отправка только после успешной генерации отчёта
            coVerifyOrder {
                generate(uuid)
                send(uuid)
            }
            confirmVerified(generate, send)
        }
    }

    /**
     * Техника тест-дизайна: #5 Error Guessing / Анализ типичных ошибок
     *
     * Автор: OwletsFox
     *
     * Описание:
     *   - Сценарий: генерация отчёта падает с исключением.
     *   - Ожидаемое поведение:
     *       1) исключение пробрасывается наружу,
     *       2) send не вызывается.
     *   - Цель: защитить инвариант "не пытаться отправлять, если отчёт не сформирован".
     */
    test("when generate fails - rethrows and does not call send") {
        runTest {
            val error = IllegalStateException("boom")
            coEvery { generate(uuid) } throws error

            shouldThrow<IllegalStateException> {
                useCase(uuid)
            }

            coVerify(exactly = 1) { generate(uuid) }
            coVerify(exactly = 0) { send(any()) }
            confirmVerified(generate, send)
        }
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: OwletsFox
     *
     * Описание:
     *   - Эквивалентный класс исходов отправки: любые не-Success результаты должны возвращаться без изменения.
     *   - Ожидаемое поведение:
     *       1) юзкейс возвращает результат send без модификации,
     *       2) generate и send вызываются по одному разу.
     *   - Цель: зафиксировать, что маппинг результата происходит только в SendShiftReportUseCase.
     */
    test("when send returns TemporaryFailure - returns the same result") {
        runTest {
            val failure = SendReportResult.TemporaryFailure(isNetwork = true)
            coEvery { generate(uuid) } returns reportJson
            coEvery { send(uuid) } returns failure

            val result = useCase(uuid)

            result shouldBe failure

            coVerify(exactly = 1) { generate(uuid) }
            coVerify(exactly = 1) { send(uuid) }
            confirmVerified(generate, send)
        }
    }
})
