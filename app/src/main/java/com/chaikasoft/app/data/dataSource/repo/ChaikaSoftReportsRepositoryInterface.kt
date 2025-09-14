package com.chaikasoft.app.data.dataSource.repo

import com.chaikasoft.app.domain.sealed.UploadResult

interface ChaikaSoftReportsRepositoryInterface {
    suspend fun uploadShiftReport(reportJson: String): UploadResult
}
