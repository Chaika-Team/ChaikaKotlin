package com.chaikasoft.app.domain.usecases.conductorTripShiftUseCases

import com.chaikasoft.app.data.room.repo.RoomConductorTripShiftRepositoryInterface
import com.chaikasoft.app.domain.models.trip.ConductorTripShiftDomain
import com.chaikasoft.app.domain.usecases.HasActiveShiftUseCase
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.test.runTest

class HasActiveShiftUseCaseTest : FunSpec({

    lateinit var repo: RoomConductorTripShiftRepositoryInterface
    lateinit var useCase: HasActiveShiftUseCase

    beforeTest {
        repo = mockk()
        useCase = HasActiveShiftUseCase(repo)
    }

    /**
     * Test design technique: #1 Equivalence classes
     *
     * Description:
     * - Input class: repo returns a non-null active shift.
     * - Expected behavior: use case returns true.
     * - Goal: verify that "active shift exists" is mapped to true.
     */
    test("when repo returns active shift - returns true") {
        runTest {
            val shift = mockk<ConductorTripShiftDomain>()
            coEvery { repo.getActiveShift() } returns shift

            val result = useCase()

            result shouldBe true

            coVerify(exactly = 1) { repo.getActiveShift() }
            confirmVerified(repo)
        }
    }

    /**
     * Test design technique: #2 Boundary values
     *
     * Description:
     * - Boundary: repo returns null (no active shift).
     * - Expected behavior: use case returns false.
     * - Goal: protect against a false-positive "active shift" state.
     */
    test("when repo returns null - returns false") {
        runTest {
            coEvery { repo.getActiveShift() } returns null

            val result = useCase()

            result shouldBe false

            coVerify(exactly = 1) { repo.getActiveShift() }
            confirmVerified(repo)
        }
    }
})
