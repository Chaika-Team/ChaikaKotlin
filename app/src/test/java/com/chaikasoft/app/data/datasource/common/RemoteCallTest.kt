package com.chaikasoft.app.data.datasource.common

import com.chaikasoft.app.domain.common.AppError
import com.chaikasoft.app.domain.common.ErrorReporter
import com.chaikasoft.app.domain.common.RemoteResult
import com.google.gson.JsonParseException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response

class RemoteCallTest : FunSpec({

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Описание:
     *   - Эквивалентный класс входа: удаленный вызов завершается успешно.
     *   - Ожидаемое поведение: результат оборачивается в RemoteResult.Success.
     *   - Цель: зафиксировать базовый happy-path контракта remoteCall.
     */
    test("when call succeeds - returns Success with data") {
        runTest {
            val result = remoteCall { 42 }

            result shouldBe RemoteResult.Success(42)
        }
    }

    /**
     * Техника тест-дизайна: #4 Переходы состояний
     *
     * Описание:
     *   - Сценарий: корутина отменена во время remoteCall.
     *   - Ожидаемое поведение: CancellationException пробрасывается, не маппится в AppError.
     *   - Цель: зафиксировать корректную семантику отмены корутин.
     */
    test("when call throws CancellationException - rethrows") {
        runTest {
            shouldThrow<CancellationException> {
                remoteCall<Int> { throw CancellationException("cancelled") }
            }
        }
    }

    /**
     * Техника тест-дизайна: #3 Таблица решений
     *
     * Описание:
     *   - Вход: HttpException с кодом 401.
     *   - Ожидаемое поведение: Unauthorized(401).
     *   - Цель: защитить правило маппинга auth-ошибок.
     */
    test("when call throws HttpException 401 - returns Unauthorized") {
        runTest {
            val http = HttpException(
                Response.error<Unit>(
                    401,
                    "unauthorized".toResponseBody("text/plain".toMediaType()),
                ),
            )

            val result = remoteCall<Unit> { throw http }
            val error = (result as RemoteResult.Failure).error as AppError.Unauthorized

            error.code shouldBe 401
            error.cause shouldBe http
        }
    }

    /**
     * Техника тест-дизайна: #3 Таблица решений
     *
     * Описание:
     *   - Вход: HttpException с кодом, отличным от 401/403.
     *   - Ожидаемое поведение: AppError.Http(code, body).
     *   - Цель: зафиксировать маппинг произвольных HTTP-ошибок.
     */
    test("when call throws HttpException 500 - returns Http with body") {
        runTest {
            val http = HttpException(
                Response.error<Unit>(
                    500,
                    "server-error".toResponseBody("text/plain".toMediaType()),
                ),
            )

            val result = remoteCall<Unit> { throw http }
            val error = (result as RemoteResult.Failure).error as AppError.Http

            error.code shouldBe 500
            error.body shouldBe "server-error"
            error.cause shouldBe http
        }
    }

    /**
     * Техника тест-дизайна: #2 Граничные значения
     *
     * Описание:
     *   - Вход: SocketTimeoutException как граница network-fail сценариев.
     *   - Ожидаемое поведение: AppError.Timeout().
     *   - Цель: разделить timeout и остальные network ошибки.
     */
    test("when call throws SocketTimeoutException - returns Timeout") {
        runTest {
            val timeout = SocketTimeoutException("timeout")

            val result = remoteCall<Unit> { throw timeout }
            val error = (result as RemoteResult.Failure).error as AppError.Timeout

            error.cause shouldBe timeout
        }
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Описание:
     *   - Вход: UnknownHostException (DNS/host недоступен).
     *   - Ожидаемое поведение: AppError.Network().
     *   - Цель: зафиксировать сетевой класс ошибок на уровне доменного результата.
     */
    test("when call throws UnknownHostException - returns Network") {
        runTest {
            val dns = UnknownHostException("dns")

            val result = remoteCall<Unit> { throw dns }
            val error = (result as RemoteResult.Failure).error as AppError.Network

            error.cause shouldBe dns
        }
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Описание:
     *   - Вход: IOException.
     *   - Ожидаемое поведение: AppError.Network().
     *   - Цель: подтвердить единый маппинг IO-сбоев в сетевую ошибку.
     */
    test("when call throws IOException - returns Network") {
        runTest {
            val io = IOException("io")

            val result = remoteCall<Unit> { throw io }
            val error = (result as RemoteResult.Failure).error as AppError.Network

            error.cause shouldBe io
        }
    }

    /**
     * Техника тест-дизайна: #5 Error Guessing
     *
     * Описание:
     *   - Вход: JsonParseException.
     *   - Ожидаемое поведение: AppError.Serialization.
     *   - Цель: зафиксировать регрессионно-опасный сценарий невалидного JSON.
     */
    test("when call throws JsonParseException - returns Serialization") {
        runTest {
            val error = JsonParseException("bad-json")

            val result = remoteCall<Unit> { throw error }

            (result as RemoteResult.Failure).error shouldBe AppError.Serialization(error)
        }
    }

    /**
     * Техника тест-дизайна: #5 Error Guessing
     *
     * Описание:
     *   - Вход: произвольный Exception.
     *   - Ожидаемое поведение: AppError.Unknown.
     *   - Цель: гарантировать fallback-ветку маппинга исключений.
     */
    test("when call throws unknown Exception - returns Unknown") {
        runTest {
            val error = IllegalStateException("boom")
            var recorded: Throwable? = null
            val reporter = ErrorReporter { recorded = it }

            val result = remoteCall(errorReporter = reporter) { throw error }

            (result as RemoteResult.Failure).error shouldBe AppError.Unknown(error)
            recorded shouldBe error
        }
    }
})
