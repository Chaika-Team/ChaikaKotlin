package com.example.chaika.data.room.repo

import com.example.chaika.data.room.dao.FastReportViewDao
import com.example.chaika.data.room.mappers.toDomain
import com.example.chaika.domain.models.FastReportDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomFastFastReportRepository @Inject constructor(
    private val fastReportViewDao: FastReportViewDao,
) : RoomFastReportRepositoryInterface {
    override fun getFastReportData(): Flow<List<FastReportDomain>> {
        return fastReportViewDao.getReportData().map { list ->
            list.map { it.toDomain() }
        }
    }
}
