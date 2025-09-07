package com.chaikasoft.app.domain.usecases

import androidx.paging.PagingData
import com.chaikasoft.app.data.dataSource.repo.ChaikaRoutesAdapterApiServiceRepositoryInterface
import com.chaikasoft.app.data.room.repo.RoomStationRepositoryInterface
import com.chaikasoft.app.domain.models.trip.StationDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
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
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    operator fun invoke(
        queryFlow: Flow<String>,
        pageSize: Int = 20
    ): Flow<PagingData<StationDomain>> =
        queryFlow
            .debounce(200)
            .distinctUntilChanged()
            .flatMapLatest { q -> repo.pagedQuery(q, pageSize) }
}

class RefreshStationsOnLaunchUseCase @Inject constructor(
    private val remoteRepo: ChaikaRoutesAdapterApiServiceRepositoryInterface,
    private val localRepo: RoomStationRepositoryInterface,
    private val hasActiveShift: HasActiveShiftUseCase
) {
    companion object {
        // Один заход «всё и сразу». Пагинация не нужна.
        private const val ALL_STATIONS_LIMIT = 100_000
    }

    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        if (hasActiveShift()) return@withContext  // нельзя трогать во время активной смены
        val stations = remoteRepo.fetchAllStations(limit = ALL_STATIONS_LIMIT)
        localRepo.upsertAll(stations) // @Upsert, быстро и идемпотентно
    }
}
