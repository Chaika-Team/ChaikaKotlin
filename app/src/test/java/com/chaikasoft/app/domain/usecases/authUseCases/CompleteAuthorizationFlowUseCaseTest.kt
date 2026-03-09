package com.chaikasoft.app.domain.usecases.authUseCases

import android.content.Intent
import com.chaikasoft.app.domain.models.ConductorDomain
import com.chaikasoft.app.domain.usecases.AuthorizeAndSaveConductorUseCase
import com.chaikasoft.app.domain.usecases.CompleteAuthorizationFlowUseCase
import com.chaikasoft.app.domain.usecases.HandleAuthorizationResponseUseCase
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.test.runTest

class CompleteAuthorizationFlowUseCaseTest : FunSpec({

    lateinit var handleAuthorizationResponseUseCase: HandleAuthorizationResponseUseCase
    lateinit var authorizeAndSaveConductorUseCase: AuthorizeAndSaveConductorUseCase
    lateinit var useCase: CompleteAuthorizationFlowUseCase
    lateinit var intent: Intent

    beforeTest {
        handleAuthorizationResponseUseCase = mockk()
        authorizeAndSaveConductorUseCase = mockk()
        useCase = CompleteAuthorizationFlowUseCase(
            handleAuthorizationResponseUseCase,
            authorizeAndSaveConductorUseCase
        )
        intent = mockk()
    }

    /**
     * Техника тест-дизайна: #3 Таблица решений
     *
     * Описание:
     * - Условия: шаг 1 возвращает token, шаг 2 возвращает conductor.
     * - Ожидаемое поведение: возвращает пару token to conductor и вызывает шаги по порядку.
     * - Цель: зафиксировать инвариант "сначала token, затем conductor".
     */
    test("when token handled and conductor authorized - returns pair") {
        runTest {
            val token = "token-123"
            val conductor = mockk<ConductorDomain>()
            coEvery { handleAuthorizationResponseUseCase(intent) } returns token
            coEvery { authorizeAndSaveConductorUseCase(token) } returns conductor

            val result = useCase(intent)

            result shouldBe (token to conductor)

            coVerifyOrder {
                handleAuthorizationResponseUseCase(intent)
                authorizeAndSaveConductorUseCase(token)
            }
            confirmVerified(handleAuthorizationResponseUseCase, authorizeAndSaveConductorUseCase)
        }
    }

    /**
     * Техника тест-дизайна: #5 Error Guessing / Анализ типичных ошибок
     *
     * Описание:
     * - Сценарий: шаг 1 падает с исключением.
     * - Ожидаемое поведение: исключение пробрасывается, шаг 2 не вызывается.
     * - Цель: защитить от частично выполненного флоу авторизации.
     */
    test("when token handling fails - rethrows and skips conductor authorization") {
        runTest {
            val error = IllegalStateException("boom")
            coEvery { handleAuthorizationResponseUseCase(intent) } throws error

            shouldThrow<IllegalStateException> {
                useCase(intent)
            }

            coVerify(exactly = 1) { handleAuthorizationResponseUseCase(intent) }
            coVerify(exactly = 0) { authorizeAndSaveConductorUseCase(any()) }
            confirmVerified(handleAuthorizationResponseUseCase, authorizeAndSaveConductorUseCase)
        }
    }

    /**
     * Техника тест-дизайна: #5 Error Guessing / Анализ типичных ошибок
     *
     * Описание:
     * - Сценарий: шаг 1 успешен, шаг 2 падает с исключением.
     * - Ожидаемое поведение: исключение пробрасывается.
     * - Цель: убедиться, что ошибки не подавляются.
     */
    test("when conductor authorization fails - rethrows") {
        runTest {
            val token = "token-123"
            val error = IllegalStateException("boom")
            coEvery { handleAuthorizationResponseUseCase(intent) } returns token
            coEvery { authorizeAndSaveConductorUseCase(token) } throws error

            shouldThrow<IllegalStateException> {
                useCase(intent)
            }

            coVerify(exactly = 1) { handleAuthorizationResponseUseCase(intent) }
            coVerify(exactly = 1) { authorizeAndSaveConductorUseCase(token) }
            confirmVerified(handleAuthorizationResponseUseCase, authorizeAndSaveConductorUseCase)
        }
    }
})
