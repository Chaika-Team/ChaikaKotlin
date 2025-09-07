package com.chaikasoft.app.domain.usecases

import com.chaikasoft.app.data.room.repo.RoomReportRepositoryInterface
import com.chaikasoft.app.domain.models.FastReportDomain
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFastReportDataUseCase @Inject constructor(
    private val reportRepository: RoomReportRepositoryInterface,
) {
    operator fun invoke(): Flow<List<FastReportDomain>> {
        return reportRepository.getFastReportData()
    }
}
