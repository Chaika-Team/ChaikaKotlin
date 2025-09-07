package com.chaikasoft.app.data.room.repo

import com.chaikasoft.app.domain.models.FastReportDomain
import kotlinx.coroutines.flow.Flow

@Suppress("FunctionInterface")
interface RoomReportRepositoryInterface {
    fun getFastReportData(): Flow<List<FastReportDomain>>
}
