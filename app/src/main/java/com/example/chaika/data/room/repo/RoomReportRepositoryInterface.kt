package com.example.chaika.data.room.repo

import com.example.chaika.domain.models.FastReportDomain
import kotlinx.coroutines.flow.Flow

@Suppress("FunctionInterface")
interface RoomReportRepositoryInterface {
    fun getFastReportData(): Flow<List<FastReportDomain>>
}
