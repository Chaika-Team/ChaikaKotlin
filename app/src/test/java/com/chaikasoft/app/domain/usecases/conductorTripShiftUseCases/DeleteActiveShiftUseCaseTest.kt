package com.chaikasoft.app.domain.usecases.conductorTripShiftUseCases

import com.chaikasoft.app.data.room.repo.RoomConductorTripShiftRepositoryInterface
import com.chaikasoft.app.domain.usecases.DeleteActiveShiftUseCase
import io.kotest.core.spec.style.FunSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.test.runTest

class DeleteActiveShiftUseCaseTest : FunSpec({

    lateinit var repository: RoomConductorTripShiftRepositoryInterface
    lateinit var useCase: DeleteActiveShiftUseCase

    beforeTest {
        repository = mockk()
        useCase = DeleteActiveShiftUseCase(repository)
    }

    test("preserve package keeps operations") {
        runTest {
            coEvery { repository.deleteActiveShift(UUID, clearOperations = false) } returns Unit

            useCase(UUID, preservePackage = true)

            coVerify(exactly = 1) {
                repository.deleteActiveShift(UUID, clearOperations = false)
            }
            confirmVerified(repository)
        }
    }

    test("do not preserve package clears operations") {
        runTest {
            coEvery { repository.deleteActiveShift(UUID, clearOperations = true) } returns Unit

            useCase(UUID, preservePackage = false)

            coVerify(exactly = 1) {
                repository.deleteActiveShift(UUID, clearOperations = true)
            }
            confirmVerified(repository)
        }
    }
}) {
    companion object {
        private const val UUID = "trip-uuid-123"
    }
}
