package com.chaikasoft.app.domain.usecases.conductorTripShiftUseCases

import com.chaikasoft.app.data.room.repo.RoomConductorTripShiftRepositoryInterface
import com.chaikasoft.app.domain.models.trip.TripShiftStatusDomain
import com.chaikasoft.app.domain.usecases.GetShiftReportJsonUseCase
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.test.runTest

class GetShiftReportJsonUseCaseTest : FunSpec({

    lateinit var repo: RoomConductorTripShiftRepositoryInterface
    lateinit var useCase: GetShiftReportJsonUseCase

    val uuid = "trip-uuid-123"

    beforeTest {
        repo = mockk()
        useCase = GetShiftReportJsonUseCase(repo)
    }

    /**
     * Test design technique: #1 Equivalence classes
     *
     * Description:
     * - Input class: repo returns a valid status with a non-blank report JSON.
     * - Expected behavior: use case returns the same pair and has no side effects.
     * - Goal: ensure no transformation is applied to data from the repository.
     */
    test("when repo returns status and report - returns the same pair") {
        runTest {
            val reportJson = """{"some":"report"}"""
            coEvery { repo.getStatusAndReport(uuid) } returns (TripShiftStatusDomain.FINISHED to reportJson)

            val result = useCase(uuid)

            result shouldBe (TripShiftStatusDomain.FINISHED to reportJson)

            coVerify(exactly = 1) { repo.getStatusAndReport(uuid) }
            confirmVerified(repo)
        }
    }

    /**
     * Test design technique: #2 Boundary values
     *
     * Description:
     * - Boundary: reportJson is null (missing report).
     * - Expected behavior: use case returns the null as-is.
     * - Goal: protect against accidental normalization of missing data.
     */
    test("when report is null - returns null report unchanged") {
        runTest {
            coEvery { repo.getStatusAndReport(uuid) } returns (TripShiftStatusDomain.FINISHED to null)

            val result = useCase(uuid)

            result shouldBe (TripShiftStatusDomain.FINISHED to null)

            coVerify(exactly = 1) { repo.getStatusAndReport(uuid) }
            confirmVerified(repo)
        }
    }
})
