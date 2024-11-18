package com.example.chaika.data.room.repo

import com.example.chaika.data.room.dao.FastReportViewDao
import com.example.chaika.data.room.mappers.toDomain
import com.example.chaika.domain.models.FastReportDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomReportRepository @Inject constructor (
    private val reportViewDao: FastReportViewDao
) : RoomReportRepositoryInterface {
    override fun getReportData(): Flow<List<FastReportDomain>> {
        return reportViewDao.getReportData().map { list ->
            list.map { it.toDomain() }
        }
    }
}
