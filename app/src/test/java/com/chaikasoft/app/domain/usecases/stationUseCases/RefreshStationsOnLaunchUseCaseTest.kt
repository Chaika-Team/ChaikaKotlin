package com.chaikasoft.app.domain.usecases.stationUseCases

import com.chaikasoft.app.data.datasource.repo.ChaikaTripperRepositoryInterface
import com.chaikasoft.app.data.room.repo.RoomStationRepositoryInterface
import com.chaikasoft.app.data.room.repo.RoomSyncMetaRepositoryInterface
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
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class RefreshStationsOnLaunchUseCaseTest : FunSpec({

    lateinit var remoteRepo: ChaikaTripperRepositoryInterface
    lateinit var localRepo: RoomStationRepositoryInterface
    lateinit var syncMetaRepo: RoomSyncMetaRepositoryInterface
    lateinit var hasActiveShift: HasActiveShiftUseCase
    lateinit var useCase: RefreshStationsOnLaunchUseCase

    beforeTest {
        remoteRepo = mockk()
        localRepo = mockk()
        syncMetaRepo = mockk()
        hasActiveShift = mockk()
        useCase = RefreshStationsOnLaunchUseCase(
            remoteRepo = remoteRepo,
            localRepo = localRepo,
            syncMetaRepo = syncMetaRepo,
            hasActiveShift = hasActiveShift,
            ioDispatcher = UnconfinedTestDispatcher(),
        )
    }

    test("when active shift exists - returns SkippedActiveShift") {
        runTest {
            coEvery { hasActiveShift() } returns true

            val result = useCase()

            result shouldBe RefreshStationsResult.SkippedActiveShift
            coVerify(exactly = 1) { hasActiveShift() }
            coVerify(exactly = 0) { localRepo.hasAnyStationsOnce() }
            coVerify(exactly = 0) { remoteRepo.fetchAllStations(any()) }
            coVerify(exactly = 0) { localRepo.upsertAll(any()) }
            coVerify(exactly = 0) { syncMetaRepo.setLastSuccessfulSyncAt(any(), any()) }
        }
    }

    test("when local stations are empty - refreshes and stores sync timestamp") {
        runTest {
            val stations = listOf(mockk<StationDomain>(), mockk())
            coEvery { hasActiveShift() } returns false
            coEvery { localRepo.hasAnyStationsOnce() } returns false
            coEvery { remoteRepo.fetchAllStations(limit = 100_000) } returns RemoteResult.Success(stations)
            coEvery { localRepo.upsertAll(stations) } returns Unit
            coEvery { syncMetaRepo.setLastSuccessfulSyncAt(any(), any()) } returns Unit

            val result = useCase()

            result shouldBe RefreshStationsResult.Success(stationCount = stations.size)
            coVerify(exactly = 1) { localRepo.hasAnyStationsOnce() }
            coVerify(exactly = 0) { syncMetaRepo.getLastSuccessfulSyncAt(any()) }
            coVerify(exactly = 1) { remoteRepo.fetchAllStations(limit = 100_000) }
            coVerify(exactly = 1) { localRepo.upsertAll(stations) }
            coVerify(exactly = 1) { syncMetaRepo.setLastSuccessfulSyncAt("stations", any()) }
        }
    }

    test("when local stations exist and ttl is not expired - returns SkippedFreshCache") {
        runTest {
            val now = System.currentTimeMillis()
            coEvery { hasActiveShift() } returns false
            coEvery { localRepo.hasAnyStationsOnce() } returns true
            coEvery { syncMetaRepo.getLastSuccessfulSyncAt("stations") } returns now - (24L * 60L * 60L * 1000L)

            val result = useCase()

            result shouldBe RefreshStationsResult.SkippedFreshCache
            coVerify(exactly = 1) { syncMetaRepo.getLastSuccessfulSyncAt("stations") }
            coVerify(exactly = 0) { remoteRepo.fetchAllStations(any()) }
            coVerify(exactly = 0) { localRepo.upsertAll(any()) }
            coVerify(exactly = 0) { syncMetaRepo.setLastSuccessfulSyncAt(any(), any()) }
        }
    }

    test("when local stations exist and ttl is expired - refreshes and stores sync timestamp") {
        runTest {
            val stations = listOf(mockk<StationDomain>())
            val expired = System.currentTimeMillis() - (91L * 24L * 60L * 60L * 1000L)
            coEvery { hasActiveShift() } returns false
            coEvery { localRepo.hasAnyStationsOnce() } returns true
            coEvery { syncMetaRepo.getLastSuccessfulSyncAt("stations") } returns expired
            coEvery { remoteRepo.fetchAllStations(limit = 100_000) } returns RemoteResult.Success(stations)
            coEvery { localRepo.upsertAll(stations) } returns Unit
            coEvery { syncMetaRepo.setLastSuccessfulSyncAt(any(), any()) } returns Unit

            val result = useCase()

            result shouldBe RefreshStationsResult.Success(stationCount = stations.size)
            coVerify(exactly = 1) { syncMetaRepo.getLastSuccessfulSyncAt("stations") }
            coVerify(exactly = 1) { remoteRepo.fetchAllStations(limit = 100_000) }
            coVerify(exactly = 1) { localRepo.upsertAll(stations) }
            coVerify(exactly = 1) { syncMetaRepo.setLastSuccessfulSyncAt("stations", any()) }
        }
    }

    test("when remote fails - returns RemoteFailure and does not update sync timestamp") {
        runTest {
            val error = AppError.Network
            coEvery { hasActiveShift() } returns false
            coEvery { localRepo.hasAnyStationsOnce() } returns false
            coEvery { remoteRepo.fetchAllStations(limit = 100_000) } returns RemoteResult.Failure(error)

            val result = useCase()

            result shouldBe RefreshStationsResult.RemoteFailure(error)
            coVerify(exactly = 1) { remoteRepo.fetchAllStations(limit = 100_000) }
            coVerify(exactly = 0) { localRepo.upsertAll(any()) }
            coVerify(exactly = 0) { syncMetaRepo.setLastSuccessfulSyncAt(any(), any()) }
        }
    }

    test("when local upsert fails - returns LocalFailure and does not update sync timestamp") {
        runTest {
            val stations = listOf(mockk<StationDomain>())
            val boom = IllegalStateException("db failure")
            coEvery { hasActiveShift() } returns false
            coEvery { localRepo.hasAnyStationsOnce() } returns false
            coEvery { remoteRepo.fetchAllStations(limit = 100_000) } returns RemoteResult.Success(stations)
            coEvery { localRepo.upsertAll(stations) } throws boom

            val result = useCase()

            result shouldBe RefreshStationsResult.LocalFailure(boom)
            coVerify(exactly = 1) { localRepo.upsertAll(stations) }
            coVerify(exactly = 0) { syncMetaRepo.setLastSuccessfulSyncAt(any(), any()) }
        }
    }
})
