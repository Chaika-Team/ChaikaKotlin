package com.chaikasoft.app.domain.usecases

import androidx.paging.PagingData
import com.chaikasoft.app.data.dataSource.repo.ChaikaTripperRepositoryInterface
import com.chaikasoft.app.data.room.repo.RoomStationRepositoryInterface
import com.chaikasoft.app.domain.common.RemoteResult
import com.chaikasoft.app.domain.models.trip.StationDomain
import com.chaikasoft.app.domain.sealed.RefreshStationsResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Возвращает Flow<PagingData<StationDomain>> по текущему тексту.
 * Обычно вызывается как: GetPagedStationSuggestionsUseCase(queryStateFlow)
 */
class GetPagedStationSuggestionsUseCase @Inject constructor(
    private val repo: RoomStationRepositoryInterface
) {
    operator fun invoke(
        query: String,
        pageSize: Int = 20
    ): Flow<PagingData<StationDomain>> =
        repo.pagedQuery(query, pageSize)
}

class RefreshStationsOnLaunchUseCase @Inject constructor(
    private val remoteRepo: ChaikaTripperRepositoryInterface,
    private val localRepo: RoomStationRepositoryInterface,
    private val hasActiveShift: HasActiveShiftUseCase
) {
    companion object {
        private const val ALL_STATIONS_LIMIT = 100_000
    }

    suspend operator fun invoke(): RefreshStationsResult = withContext(Dispatchers.IO) {
        // 1) Бизнес-правило: во время активной смены не трогаем станции
        if (hasActiveShift()) return@withContext RefreshStationsResult.SkippedActiveShift

        // 2) Сеть: получаем RemoteResult
        when (val remote = remoteRepo.fetchAllStations(limit = ALL_STATIONS_LIMIT)) {
            is RemoteResult.Failure -> {
                return@withContext RefreshStationsResult.RemoteFailure(remote.error)
            }

            is RemoteResult.Success -> {
                // 3) Локальная БД: upsert может упасть (RoomException/SQLiteException)
                return@withContext try {
                    localRepo.upsertAll(remote.data)
                    RefreshStationsResult.Success(stationCount = remote.data.size)
                } catch (e: Exception) {
                    RefreshStationsResult.LocalFailure(e)
                }
            }
        }
    }
}
