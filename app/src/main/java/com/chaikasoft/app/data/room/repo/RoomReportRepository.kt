package com.chaikasoft.app.data.room.repo

import com.chaikasoft.app.data.room.dao.FastReportViewDao
import com.chaikasoft.app.data.room.mappers.toDomain
import com.chaikasoft.app.domain.models.FastReportDomain
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomReportRepository @Inject constructor(private val fastReportViewDao: FastReportViewDao) :
    RoomReportRepositoryInterface {
    override fun getFastReportData(): Flow<List<FastReportDomain>> =
        fastReportViewDao.getReportData().map { list ->
            list.map { it.toDomain() }
        }
}
