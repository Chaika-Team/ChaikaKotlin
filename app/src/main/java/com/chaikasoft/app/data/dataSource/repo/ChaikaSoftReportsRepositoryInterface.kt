package com.chaikasoft.app.data.datasource.repo

import com.chaikasoft.app.domain.sealed.UploadResult

interface ChaikaSoftReportsRepositoryInterface {
    suspend fun uploadShiftReport(reportJson: String): UploadResult
}
