package com.example.chaika.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.chaika.data.room.entities.FastReportView
import kotlinx.coroutines.flow.Flow

@Dao
interface FastReportViewDao {
    @Query("SELECT * FROM fast_report_view")
    fun getReportData(): Flow<List<FastReportView>>
}
