package com.chaikasoft.app.data.room.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.chaikasoft.app.data.room.dao.StationDao
import com.chaikasoft.app.data.room.mappers.toDomain
import com.chaikasoft.app.data.room.mappers.toEntity
import com.chaikasoft.app.domain.models.trip.StationDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomStationRepository @Inject constructor(
    private val dao: StationDao
) : RoomStationRepositoryInterface {

    override suspend fun upsertAll(stations: List<StationDomain>) =
        dao.upsertAll(stations.map { it.toEntity() })

    override fun pagedQuery(
        query: String,
        pageSize: Int
    ): Flow<PagingData<StationDomain>> {
        if (query.isBlank()) return flowOf(PagingData.empty())

        val prefix = "${escapeLike(query)}%"

        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                initialLoadSize = pageSize,
                prefetchDistance = 5,
                enablePlaceholders = false,
                maxSize = pageSize * 3
            ),
            pagingSourceFactory = { dao.pagingByQuery(prefix) }
        ).flow.map { paging -> paging.map { it.toDomain() } }
    }

    private fun escapeLike(s: String): String =
        s.replace("\\", "\\\\")
            .replace("%", "\\%")
            .replace("_", "\\_")

    override suspend fun getByCode(code: Int): StationDomain? =
        dao.getByCode(code)?.toDomain()
}
