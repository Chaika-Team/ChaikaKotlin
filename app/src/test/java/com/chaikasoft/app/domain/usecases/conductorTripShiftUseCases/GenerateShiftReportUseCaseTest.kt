package com.chaikasoft.app.domain.usecases.conductorTripShiftUseCases

import com.chaikasoft.app.data.room.repo.RoomShiftReportRepositoryInterface
import com.chaikasoft.app.domain.usecases.GenerateShiftReportUseCase
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.test.runTest

class GenerateShiftReportUseCaseTest : FunSpec({

    lateinit var repository: RoomShiftReportRepositoryInterface
    lateinit var useCase: GenerateShiftReportUseCase

    val uuid = "trip-uuid-123"
    val reportJson = """{"trip_id":{"route_id":"TN-01","start_time":"2025-01-01T00:00:00Z"}}"""

    beforeTest {
        repository = mockk()
        useCase = GenerateShiftReportUseCase(repository)
    }

    /**
     * Test-design technique: #7 Decision table
     *
     * GenerateShiftReportUseCase is now a domain facade over the transactional data-layer
     * operation. The detailed DB invariants live in RoomShiftReportRepository tests.
     */
    test("delegates atomic report generation to repository and returns persisted json") {
        runTest {
            coEvery { repository.finishShiftWithReport(uuid) } returns reportJson

            val result = useCase(uuid)

            result shouldBe reportJson
            coVerify(exactly = 1) { repository.finishShiftWithReport(uuid) }
            confirmVerified(repository)
        }
    }

    /**
     * Test-design technique: #5 Error guessing.
     * Repository errors must stay visible to CompleteShiftAndSendUseCase, which prevents send.
     */
    test("rethrows repository failure") {
        runTest {
            val error = IllegalStateException("Shift with uuid=$uuid not found")
            coEvery { repository.finishShiftWithReport(uuid) } throws error

            shouldThrow<IllegalStateException> {
                useCase(uuid)
            }

            coVerify(exactly = 1) { repository.finishShiftWithReport(uuid) }
            confirmVerified(repository)
        }
    }
})
