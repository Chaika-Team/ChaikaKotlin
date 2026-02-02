package com.chaikasoft.app.data.room.repo


import androidx.paging.PagingData

import com.chaikasoft.app.domain.models.trip.StationDomain
import kotlinx.coroutines.flow.Flow

interface RoomStationRepositoryInterface {
    suspend fun upsertAll(stations: List<StationDomain>)
    fun pagedQuery(query: String, pageSize: Int): Flow<PagingData<StationDomain>>
    suspend fun getByCode(code: String): StationDomain?
}