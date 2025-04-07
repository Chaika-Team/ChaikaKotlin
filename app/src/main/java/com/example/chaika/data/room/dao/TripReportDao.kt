package com.example.chaika.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.chaika.data.room.entities.TripReportEntity

@Dao
interface TripReportDao {
    @Insert
    suspend fun insertReport(report: TripReportEntity): Long

    @Query("SELECT * FROM trip_reports WHERE is_sent = 0")
    suspend fun getPendingReports(): List<TripReportEntity>

    @Query("UPDATE trip_reports SET is_sent = :isSent WHERE id = :reportId")
    suspend fun updateReportStatus(reportId: Long, isSent: Boolean): Int
}
