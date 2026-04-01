package com.chaikasoft.app.domain.usecases.authUseCases

import android.content.Intent
import com.chaikasoft.app.auth.OAuthManager
import com.chaikasoft.app.data.crypto.EncryptedTokenManagerInterface
import com.chaikasoft.app.domain.usecases.HandleAuthorizationResponseUseCase
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.mockk

import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest

class HandleAuthorizationResponseUseCaseTest : FunSpec({

    lateinit var oAuthManager: OAuthManager
    lateinit var tokenManager: EncryptedTokenManagerInterface
    lateinit var useCase: HandleAuthorizationResponseUseCase
    lateinit var intent: Intent

    beforeTest {
        oAuthManager = mockk()
        tokenManager = mockk()
        useCase = HandleAuthorizationResponseUseCase(
            oAuthManager = oAuthManager,
            tokenManager = tokenManager,
            ioDispatcher = UnconfinedTestDispatcher(),
        )
        intent = mockk()
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Описание:
     * - Класс входных данных: OAuth возвращает непустой token.
     * - Ожидаемое поведение: token сохраняется и возвращается.
     * - Цель: проверить happy path и сайд-эффект.
     */
    test("when oauth returns token - saves token and returns it") {
        runTest {
            val token = "token-123"
            every { oAuthManager.handleAuthorizationResponse(intent, any()) } answers {
                val callback = secondArg<(String) -> Unit>()
                callback(token)
            }
            every { tokenManager.saveToken(token) } just Runs

            val result = useCase(intent)

            result shouldBe token

            verify(exactly = 1) { oAuthManager.handleAuthorizationResponse(intent, any()) }
            verify(exactly = 1) { tokenManager.saveToken(token) }
            confirmVerified(oAuthManager, tokenManager)
        }
    }

    /**
     * Техника тест-дизайна: #2 Граничные значения
     *
     * Описание:
     * - Граница: OAuth возвращает пустой token.
     * - Ожидаемое поведение: выбрасывается IllegalStateException, token не сохраняется.
     * - Цель: защититься от сохранения невалидных токенов.
     */
    test("when oauth returns empty token - throws") {
        runTest {
            every { oAuthManager.handleAuthorizationResponse(intent, any()) } answers {
                val callback = secondArg<(String) -> Unit>()
                callback("")
            }
            every { tokenManager.saveToken(any()) } just Runs

            shouldThrow<IllegalStateException> {
                useCase(intent)
            }

            verify(exactly = 1) { oAuthManager.handleAuthorizationResponse(intent, any()) }
            verify(exactly = 0) { tokenManager.saveToken(any()) }
            confirmVerified(oAuthManager, tokenManager)
        }
    }

    /**
     * Техника тест-дизайна: #4 Переходы состояний
     *
     * Описание:
     * - Сценарий: корутина отменяется до OAuth-колбэка.
     * - Ожидаемое поведение: колбэк не resume-ит continuation, token не сохраняется.
     * - Цель: зафиксировать контракт отмены с проверкой continuation.isActive.
     */
    test("when coroutine is cancelled before callback - does not resume or save") {
        runTest {
            val callbackSlot = slot<(String) -> Unit>()
            every { oAuthManager.handleAuthorizationResponse(intent, capture(callbackSlot)) } answers { }
            every { tokenManager.saveToken(any()) } just Runs

            val deferred = async { useCase(intent) }
            runCurrent()
            deferred.cancel()
            runCurrent()

            callbackSlot.captured("token-123")

            shouldThrow<CancellationException> {
                deferred.await()
            }

            verify(exactly = 1) { oAuthManager.handleAuthorizationResponse(intent, any()) }
            verify(exactly = 0) { tokenManager.saveToken(any()) }
            confirmVerified(oAuthManager, tokenManager)
        }
    }
})
