package com.chaikasoft.app.domain.usecases

import android.util.Log
import androidx.paging.PagingData
import com.chaikasoft.app.data.datasource.repo.ChaikaTripperRepositoryInterface
import com.chaikasoft.app.data.room.repo.RoomStationRepositoryInterface
import com.chaikasoft.app.data.room.repo.RoomSyncMetaRepositoryInterface
import com.chaikasoft.app.data.room.sync.SyncDataset
import com.chaikasoft.app.di.IoDispatcher
import com.chaikasoft.app.domain.common.RemoteResult
import com.chaikasoft.app.domain.models.trip.StationDomain
import com.chaikasoft.app.domain.sealed.RefreshStationsResult
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Возвращает Flow<PagingData<StationDomain>> по текущему тексту.
 * Обычно вызывается как: GetPagedStationSuggestionsUseCase(queryStateFlow)
 */
class GetPagedStationSuggestionsUseCase @Inject constructor(
    private val repo: RoomStationRepositoryInterface
) {
    operator fun invoke(query: String, pageSize: Int = 20): Flow<PagingData<StationDomain>> =
        repo.pagedQuery(query, pageSize)
}

class RefreshStationsOnLaunchUseCase @Inject constructor(
    private val remoteRepo: ChaikaTripperRepositoryInterface,
    private val localRepo: RoomStationRepositoryInterface,
    private val syncMetaRepo: RoomSyncMetaRepositoryInterface,
    private val hasActiveShift: HasActiveShiftUseCase,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    companion object {
        private const val ALL_STATIONS_LIMIT = 100_000
    }

    /**
     * Converts any non-cancellation local persistence failure into the refresh boundary result.
     */
    @Suppress("TooGenericExceptionCaught")
    suspend operator fun invoke(): RefreshStationsResult = withContext(ioDispatcher) {
        if (hasActiveShift()) return@withContext RefreshStationsResult.SkippedActiveShift

        val shouldRefresh = shouldRefreshStations()
        if (!shouldRefresh) return@withContext RefreshStationsResult.SkippedFreshCache

        return@withContext when (
            val remote = remoteRepo.fetchAllStations(
                limit = ALL_STATIONS_LIMIT
            )
        ) {
            is RemoteResult.Failure -> RefreshStationsResult.RemoteFailure(remote.error)

            is RemoteResult.Success -> {
                try {
                    localRepo.upsertAll(remote.data)
                    syncMetaRepo.setLastSuccessfulSyncAt(
                        datasetKey = SyncDataset.STATIONS.key,
                        timestampMillis = System.currentTimeMillis()
                    )
                    RefreshStationsResult.Success(stationCount = remote.data.size)
                } catch (cancellation: CancellationException) {
                    throw cancellation
                } catch (e: Exception) {
                    RefreshStationsResult.LocalFailure(e)
                }
            }
        }
    }

    private suspend fun shouldRefreshStations(): Boolean {
        val hasAnyStations = localRepo.hasAnyStationsOnce()
        if (!hasAnyStations) return true

        val lastSuccessfulSyncAt =
            syncMetaRepo.getLastSuccessfulSyncAt(SyncDataset.STATIONS.key) ?: return true

        val now = System.currentTimeMillis()
        Log.d("StationUseCases_shouldRefreshStations", "current time: $now")
        Log.d("StationUseCases_shouldRefreshStations", "lastSynx: $lastSuccessfulSyncAt")
        return now - lastSuccessfulSyncAt >= SyncDataset.STATIONS.ttlMs
    }
}
