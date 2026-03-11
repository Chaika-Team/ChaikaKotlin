package com.chaikasoft.app.domain.usecases.operationUseCases

import com.chaikasoft.app.data.room.repo.RoomCartOperationRepositoryInterface
import com.chaikasoft.app.domain.usecases.ClearOperationsAndPackageUseCase
import io.kotest.core.spec.style.FunSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.test.runTest

class ClearOperationsAndPackageUseCaseTest : FunSpec({

    lateinit var repo: RoomCartOperationRepositoryInterface
    lateinit var useCase: ClearOperationsAndPackageUseCase

    beforeTest {
        repo = mockk()
        useCase = ClearOperationsAndPackageUseCase(repo)
    }

    /**
     * Техника тест-дизайна: #5 Error guessing
     *
     * Автор: Codex
     *
     * Описание:
     *   - Сценарий: вызывается "красная кнопка" очистки.
     *   - Ожидаемое поведение: repo.clearAllOperations() вызывается ровно один раз.
     *   - Цель: убедиться, что разрушительное действие запускается надежно.
     */
    test("when invoked - clears all operations exactly once") {
        runTest {
            coEvery { repo.clearAllOperations() } returns Unit

            useCase()

            coVerify(exactly = 1) { repo.clearAllOperations() }
            confirmVerified(repo)
        }
    }
})
