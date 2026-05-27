package com.chaikasoft.app.domain.usecases.operationUseCases

import com.chaikasoft.app.data.room.repo.RoomCartOperationRepositoryInterface
import com.chaikasoft.app.domain.usecases.ClearCartOperationsUseCase
import io.kotest.core.spec.style.FunSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.test.runTest

class ClearCartOperationsUseCaseTest : FunSpec({

    lateinit var repo: RoomCartOperationRepositoryInterface
    lateinit var useCase: ClearCartOperationsUseCase

    beforeTest {
        repo = mockk()
        useCase = ClearCartOperationsUseCase(repo)
    }

    test("when invoked - clears all operations exactly once") {
        runTest {
            coEvery { repo.clearAllOperations() } returns Unit

            useCase()

            coVerify(exactly = 1) { repo.clearAllOperations() }
            confirmVerified(repo)
        }
    }
})
