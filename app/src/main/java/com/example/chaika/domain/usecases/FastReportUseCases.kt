package com.example.chaika.domain.usecases

import com.example.chaika.data.room.repo.RoomFastReportRepositoryInterface
import com.example.chaika.domain.models.FastReportDomain
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFastReportDataUseCase @Inject constructor(
    private val reportRepository: RoomFastReportRepositoryInterface,
) {
    operator fun invoke(): Flow<List<FastReportDomain>> {
        return reportRepository.getFastReportData()
    }
}
