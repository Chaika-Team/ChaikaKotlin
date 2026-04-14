package com.chaikasoft.app.domain.usecases

import com.chaikasoft.app.data.room.repo.RoomReportRepositoryInterface
import com.chaikasoft.app.domain.models.FastReportDomain
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetFastReportDataUseCase @Inject constructor(
    private val reportRepository: RoomReportRepositoryInterface
) {
    operator fun invoke(): Flow<List<FastReportDomain>> = reportRepository.getFastReportData()
}
