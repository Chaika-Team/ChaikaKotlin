package com.chaikasoft.app.data.room.repo

import com.chaikasoft.app.data.room.dao.FastReportViewDao
import com.chaikasoft.app.data.room.mappers.toDomain
import com.chaikasoft.app.domain.models.FastReportDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomReportRepository @Inject constructor(
    private val fastReportViewDao: FastReportViewDao,
) : RoomReportRepositoryInterface {
    override fun getFastReportData(): Flow<List<FastReportDomain>> {
        return fastReportViewDao.getReportData().map { list ->
            list.map { it.toDomain() }
        }
    }
}
