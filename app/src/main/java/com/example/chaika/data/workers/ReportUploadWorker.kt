// ReportUploadWorker.kt
package com.example.chaika.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.example.chaika.data.room.repo.RoomTripReportRepositoryInterface
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.coroutineScope

@HiltWorker
class ReportUploadWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val reportRepository: RoomTripReportRepositoryInterface
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result = coroutineScope {
        // Проверка наличия сети – здесь можно использовать реальные методы проверки
        if (!hasNetworkConnection()) {
            return@coroutineScope Result.retry()
        }

        // Получаем все неподтверждённые отчёты из базы данных
        val pendingReports = reportRepository.getPendingTripReports()
        // Если нечего отправлять, считаем работу успешной
        if (pendingReports.isEmpty()) return@coroutineScope Result.success()

        // Для каждого отчёта пытаемся выполнить отправку
        for ((reportId, tripReport) in pendingReports) {
            val success = reportRepository.uploadTripReport(tripReport)
            if (success) {
                // Если отправка успешна, обновляем статус отчёта в базе
                reportRepository.markReportAsSent(reportId)
            } else {
                // Если хотя бы один отчёт не отправился, возвращаем retry
                return@coroutineScope Result.retry()
            }
        }
        Result.success()
    }

    // Пример простой проверки сети – для реальной реализации используйте ConnectivityManager
    private fun hasNetworkConnection(): Boolean = true
}
