package com.chaikasoft.app.domain.usecases.stationUseCases

import com.chaikasoft.app.data.dataSource.repo.ChaikaTripperRepositoryInterface
import com.chaikasoft.app.data.room.repo.RoomStationRepositoryInterface
import com.chaikasoft.app.domain.common.AppError
import com.chaikasoft.app.domain.common.RemoteResult
import com.chaikasoft.app.domain.models.trip.StationDomain
import com.chaikasoft.app.domain.sealed.RefreshStationsResult
import com.chaikasoft.app.domain.usecases.HasActiveShiftUseCase
import com.chaikasoft.app.domain.usecases.RefreshStationsOnLaunchUseCase
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest

class RefreshStationsOnLaunchUseCaseTest : FunSpec({

    lateinit var remoteRepo: ChaikaTripperRepositoryInterface
    lateinit var localRepo: RoomStationRepositoryInterface
    lateinit var hasActiveShift: HasActiveShiftUseCase
    lateinit var useCase: RefreshStationsOnLaunchUseCase

    beforeTest {
        remoteRepo = mockk()
        localRepo = mockk()
        hasActiveShift = mockk()
        useCase = RefreshStationsOnLaunchUseCase(
            remoteRepo = remoteRepo,
            localRepo = localRepo,
            hasActiveShift = hasActiveShift,
            ioDispatcher = UnconfinedTestDispatcher(),
        )
    }

    /**
     * Test design technique: #1 Equivalence classes
     *
     * Description:
     * - Input class: active shift exists.
     * - Expected behavior: refresh is skipped and no repos are touched.
     * - Goal: protect the "do not refresh during active shift" rule.
     */
    test("when active shift exists - returns SkippedActiveShift") {
        runTest {
            coEvery { hasActiveShift() } returns true

            val result = useCase()

            result shouldBe RefreshStationsResult.SkippedActiveShift

            coVerify(exactly = 1) { hasActiveShift() }
            coVerify(exactly = 0) { remoteRepo.fetchAllStations(any()) }
            coVerify(exactly = 0) { localRepo.upsertAll(any()) }
            confirmVerified(hasActiveShift, remoteRepo, localRepo)
        }
    }

    /**
     * Test design technique: #3 Decision table
     *
     * Description:
     * - Conditions: no active shift, remote returns Failure.
     * - Expected behavior: maps to RemoteFailure and does not touch local DB.
     * - Goal: protect failure propagation from remote layer.
     */
    test("when remote fails - returns RemoteFailure") {
        runTest {
            val error = AppError.Network
            coEvery { hasActiveShift() } returns false
            coEvery { remoteRepo.fetchAllStations(limit = 100_000) } returns RemoteResult.Failure(error)

            val result = useCase()

            result shouldBe RefreshStationsResult.RemoteFailure(error)

            coVerify(exactly = 1) { hasActiveShift() }
            coVerify(exactly = 1) { remoteRepo.fetchAllStations(limit = 100_000) }
            coVerify(exactly = 0) { localRepo.upsertAll(any()) }
            confirmVerified(hasActiveShift, remoteRepo, localRepo)
        }
    }

    /**
     * Test design technique: #3 Decision table
     *
     * Description:
     * - Conditions: no active shift, remote returns Success, local upsert succeeds.
     * - Expected behavior: returns Success with station count.
     * - Goal: protect happy-path mapping and count.
     */
    test("when remote succeeds and local upsert succeeds - returns Success") {
        runTest {
            val stations = listOf(mockk<StationDomain>(), mockk(), mockk())
            coEvery { hasActiveShift() } returns false
            coEvery { remoteRepo.fetchAllStations(limit = 100_000) } returns RemoteResult.Success(stations)
            coEvery { localRepo.upsertAll(stations) } returns Unit

            val result = useCase()

            result shouldBe RefreshStationsResult.Success(stationCount = stations.size)

            coVerify(exactly = 1) { hasActiveShift() }
            coVerify(exactly = 1) { remoteRepo.fetchAllStations(limit = 100_000) }
            coVerify(exactly = 1) { localRepo.upsertAll(stations) }
            confirmVerified(hasActiveShift, remoteRepo, localRepo)
        }
    }

    /**
     * Test design technique: #2 Boundary values
     *
     * Description:
     * - Boundary: local upsert throws exception.
     * - Expected behavior: maps to LocalFailure with same cause.
     * - Goal: protect the "DB failure is surfaced" rule.
     */
    test("when local upsert throws - returns LocalFailure") {
        runTest {
            val stations = listOf(mockk<StationDomain>())
            val boom = IllegalStateException("db failure")
            coEvery { hasActiveShift() } returns false
            coEvery { remoteRepo.fetchAllStations(limit = 100_000) } returns RemoteResult.Success(stations)
            coEvery { localRepo.upsertAll(stations) } throws boom

            val result = useCase()

            result shouldBe RefreshStationsResult.LocalFailure(boom)

            coVerify(exactly = 1) { hasActiveShift() }
            coVerify(exactly = 1) { remoteRepo.fetchAllStations(limit = 100_000) }
            coVerify(exactly = 1) { localRepo.upsertAll(stations) }
            confirmVerified(hasActiveShift, remoteRepo, localRepo)
        }
    }
})
