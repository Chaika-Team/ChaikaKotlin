package com.chaikasoft.app.domain.usecases.conductorTripShiftUseCases

import com.chaikasoft.app.data.room.repo.RoomConductorTripShiftRepositoryInterface
import com.chaikasoft.app.domain.models.trip.TripShiftStatusDomain
import com.chaikasoft.app.domain.usecases.MarkShiftSentUseCase
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest

class MarkShiftSentUseCaseTest : FunSpec({

    lateinit var repo: RoomConductorTripShiftRepositoryInterface
    lateinit var useCase: MarkShiftSentUseCase

    val uuid = "trip-uuid-123"

    beforeTest {
        repo = mockk()
        useCase = MarkShiftSentUseCase(repo)
    }

    /**
     * Test design technique: #4 State transition.
     *
     * Scenario:
     *   - When the use case is invoked, the shift should move to SENT.
     *
     * Expected behavior:
     *   1) updateStatusAndReport is called with SENT status,
     *   2) reportJson is not overwritten (null),
     *   3) updatedAt is set to a non-zero timestamp.
     */
    test("when invoked - marks shift as SENT and updates timestamp") {
        runTest {
            // Given
            val updatedAtSlot = slot<Long>()
            coEvery {
                repo.updateStatusAndReport(
                    uuid = uuid,
                    newStatus = TripShiftStatusDomain.SENT.code,
                    reportJson = null,
                    updatedAt = capture(updatedAtSlot)
                )
            } returns Unit

            // When
            useCase(uuid)

            // Then
            (updatedAtSlot.captured > 0L) shouldBe true
            coVerify(exactly = 1) {
                repo.updateStatusAndReport(
                    uuid = uuid,
                    newStatus = TripShiftStatusDomain.SENT.code,
                    reportJson = null,
                    updatedAt = any()
                )
            }
            confirmVerified(repo)
        }
    }
})
