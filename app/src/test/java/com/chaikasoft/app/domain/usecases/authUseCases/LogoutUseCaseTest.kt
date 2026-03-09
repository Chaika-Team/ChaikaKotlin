package com.chaikasoft.app.domain.usecases.authUseCases

import com.chaikasoft.app.data.crypto.EncryptedTokenManagerInterface
import com.chaikasoft.app.data.local.ImageSubDir
import com.chaikasoft.app.data.local.LocalImageRepositoryInterface
import com.chaikasoft.app.domain.sealed.LogoutResult
import com.chaikasoft.app.domain.usecases.DeleteAllConductorsUseCase
import com.chaikasoft.app.domain.usecases.HasActiveShiftUseCase
import com.chaikasoft.app.domain.usecases.LogoutUseCase
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest

class LogoutUseCaseTest : FunSpec({

    lateinit var tokenManager: EncryptedTokenManagerInterface
    lateinit var deleteAllConductorsUseCase: DeleteAllConductorsUseCase
    lateinit var imageRepository: LocalImageRepositoryInterface
    lateinit var hasActiveShiftUseCase: HasActiveShiftUseCase
    lateinit var useCase: LogoutUseCase

    beforeTest {
        tokenManager = mockk()
        deleteAllConductorsUseCase = mockk()
        imageRepository = mockk()
        hasActiveShiftUseCase = mockk()
        useCase = LogoutUseCase(
            tokenManager,
            deleteAllConductorsUseCase,
            imageRepository,
            hasActiveShiftUseCase
        )
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Описание:
     * - Класс входных данных: активная смена существует.
     * - Ожидаемое поведение: возвращает ActiveShiftExists и пропускает очистку.
     * - Цель: защитить правило "нельзя логаутиться при активной смене".
     */
    test("when active shift exists - returns ActiveShiftExists and skips cleanup") {
        runTest {
            coEvery { hasActiveShiftUseCase() } returns true

            val result = useCase()

            result shouldBe LogoutResult.ActiveShiftExists

            coVerify(exactly = 1) { hasActiveShiftUseCase() }
            verify(exactly = 0) { tokenManager.clearToken() }
            coVerify(exactly = 0) { deleteAllConductorsUseCase() }
            coVerify(exactly = 0) { imageRepository.deleteImagesInSubDir(any()) }
            confirmVerified(
                tokenManager,
                deleteAllConductorsUseCase,
                imageRepository,
                hasActiveShiftUseCase
            )
        }
    }

    /**
     * Техника тест-дизайна: #3 Таблица решений
     *
     * Описание:
     * - Условия: активной смены нет, все шаги очистки успешны.
     * - Ожидаемое поведение: очищает token, удаляет проводников и изображения, возвращает Success.
     * - Цель: убедиться, что обязательные сайд-эффекты логаута выполняются.
     */
    test("when no active shift and cleanup succeeds - returns Success") {
        runTest {
            coEvery { hasActiveShiftUseCase() } returns false
            every { tokenManager.clearToken() } just Runs
            coEvery { deleteAllConductorsUseCase() } just Runs
            coEvery { imageRepository.deleteImagesInSubDir(ImageSubDir.CONDUCTORS.folder) } returns true

            val result = useCase()

            result shouldBe LogoutResult.Success

            coVerify(exactly = 1) { hasActiveShiftUseCase() }
            verify(exactly = 1) { tokenManager.clearToken() }
            coVerify(exactly = 1) { deleteAllConductorsUseCase() }
            coVerify(exactly = 1) { imageRepository.deleteImagesInSubDir(ImageSubDir.CONDUCTORS.folder) }
            confirmVerified(
                tokenManager,
                deleteAllConductorsUseCase,
                imageRepository,
                hasActiveShiftUseCase
            )
        }
    }

    /**
     * Техника тест-дизайна: #5 Error Guessing / Анализ типичных ошибок
     *
     * Описание:
     * - Сценарий: очистка падает после очистки token.
     * - Ожидаемое поведение: возвращает Failure с reason и останавливает дальнейшую очистку.
     * - Цель: убедиться, что ошибки не подавляются и корректно отражаются в результате.
     */
    test("when cleanup step fails - returns Failure and skips remaining steps") {
        runTest {
            val error = IllegalStateException("boom")
            coEvery { hasActiveShiftUseCase() } returns false
            every { tokenManager.clearToken() } just Runs
            coEvery { deleteAllConductorsUseCase() } throws error

            val result = useCase()

            result shouldBe LogoutResult.Failure("boom")

            coVerify(exactly = 1) { hasActiveShiftUseCase() }
            verify(exactly = 1) { tokenManager.clearToken() }
            coVerify(exactly = 1) { deleteAllConductorsUseCase() }
            coVerify(exactly = 0) { imageRepository.deleteImagesInSubDir(any()) }
            confirmVerified(
                tokenManager,
                deleteAllConductorsUseCase,
                imageRepository,
                hasActiveShiftUseCase
            )
        }
    }
})
