package com.example.chaika.data.room.repo

import com.example.chaika.domain.models.TripReport

interface RoomTripReportRepositoryInterface {
    suspend fun saveTripReport(report: TripReport): Long

    // Возвращаем список пар (ID отчёта, TripReport)
    suspend fun getPendingTripReports(): List<Pair<Long, TripReport>>
    suspend fun markReportAsSent(reportId: Long)

    // Эмуляция сетевой отправки отчёта
    suspend fun uploadTripReport(report: TripReport): Boolean
}
