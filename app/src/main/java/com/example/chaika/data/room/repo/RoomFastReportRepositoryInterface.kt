package com.example.chaika.data.room.repo

import com.example.chaika.domain.models.FastReportDomain
import kotlinx.coroutines.flow.Flow

@Suppress("FunctionInterface")
interface RoomFastReportRepositoryInterface {
    fun getFastReportData(): Flow<List<FastReportDomain>>
}
