package com.chaikasoft.app.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.chaikasoft.app.data.room.entities.FastReportView
import kotlinx.coroutines.flow.Flow

@Suppress("FunctionInterface")
@Dao
interface FastReportViewDao {
    @Query("SELECT * FROM fast_report_view")
    fun getReportData(): Flow<List<FastReportView>>
}
