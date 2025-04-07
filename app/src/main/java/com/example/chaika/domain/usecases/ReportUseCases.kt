// GenerateTripReportUseCase.kt (обновлённый вариант)
package com.example.chaika.domain.usecases

import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.chaika.data.room.repo.RoomCartItemRepositoryInterface
import com.example.chaika.data.room.repo.RoomCartOperationRepositoryInterface
import com.example.chaika.data.room.repo.RoomConductorRepositoryInterface
import com.example.chaika.data.room.repo.RoomTripReportRepositoryInterface
import com.example.chaika.data.workers.ReportUploadWorker
import com.example.chaika.domain.models.TripReport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GenerateTripReportUseCase @Inject constructor(
    private val cartOperationRepository: RoomCartOperationRepositoryInterface,
    private val cartItemRepository: RoomCartItemRepositoryInterface,
    private val conductorRepository: RoomConductorRepositoryInterface,
    private val tripReportRepository: RoomTripReportRepositoryInterface
) {

    suspend operator fun invoke(): Boolean = withContext(Dispatchers.IO) {
        try {
            // Тестовые данные поездки
            val routeID = "119A"
            val startTime = "2024-10-15T00:12:00+03:00"
            val endTime = "2024-10-15T09:52:00+03:00"
            val carriageID = "12"

            // Получаем операции и их ID
            val cartOperationReportsWithIds = cartOperationRepository
                .getCartOperationReportsWithIds()
                .first()

            // Создаём список операций с заполненными товарами и корректным employeeID
            val cartOperationReports =
                cartOperationReportsWithIds.map { (operationId, operationReport) ->
                    // Получаем реальные employeeID через conductorRepository
                    val employeeID =
                        conductorRepository.getEmployeeIDByConductorId(operationReport.employeeID.toInt())
                    // Загружаем связанные элементы корзины
                    val cartItems = cartItemRepository
                        .getCartItemReportsByOperationId(operationId)
                        .first()
                    // Обновляем CartOperationReport
                    operationReport.copy(
                        employeeID = employeeID,
                        items = cartItems
                    )
                }

            // Формируем TripReport (свойство carts будет сериализовано в поле carts_json)
            val tripReport = TripReport(
                routeID = routeID,
                startTime = startTime,
                endTime = endTime,
                carriageID = carriageID,
                carts = cartOperationReports
            )

            // Сохраняем отчёт в базу данных
            tripReportRepository.saveTripReport(tripReport)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

class EnqueueSendTripReportUseCase @Inject constructor(
    private val reportRepository: RoomTripReportRepositoryInterface,
    private val workManager: WorkManager
) {
    /**
     * Принимает доменную модель TripReport, сохраняет её в таблицу (с флагом isSent = false)
     * и ставит задачу для отправки через WorkManager.
     */
    suspend operator fun invoke(tripReport: TripReport) {
        // Сохраняем отчёт в базу данных
        reportRepository.saveTripReport(tripReport)

        // Определяем ограничения: требуется подключение к сети
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Создаём OneTimeWorkRequest для ReportUploadWorker
        val workRequest = OneTimeWorkRequestBuilder<ReportUploadWorker>()
            .setConstraints(constraints)
            .build()

        // Используем уникальное имя для очереди, чтобы избежать дублирования задач
        workManager.enqueueUniqueWork(
            "reportUploadWork",
            ExistingWorkPolicy.APPEND,
            workRequest
        )
    }
}
