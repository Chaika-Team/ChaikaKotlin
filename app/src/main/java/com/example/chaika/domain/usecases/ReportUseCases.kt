package com.example.chaika.domain.usecases

import com.example.chaika.data.local.LocalTripReportRepository
import com.example.chaika.data.room.repo.RoomCartItemRepositoryInterface
import com.example.chaika.data.room.repo.RoomCartOperationRepositoryInterface
import com.example.chaika.data.room.repo.RoomConductorRepositoryInterface
import com.example.chaika.domain.models.TripReport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

// TODO: сохранять время генерации отчёта, записывать уникальный серийник товара вместо локального id

class GenerateTripReportUseCase @Inject constructor(
    private val cartOperationRepository: RoomCartOperationRepositoryInterface,
    private val cartItemRepository: RoomCartItemRepositoryInterface,
    private val conductorRepository: RoomConductorRepositoryInterface,
    private val tripReportRepository: LocalTripReportRepository
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

            // Создаём список операций с заполненными товарами и корректным EmployeeID
            val cartOperationReports =
                cartOperationReportsWithIds.map { (operationId, operationReport) ->
                    // Получаем реальные employeeID через conductorRepository
                    val employeeID =
                        conductorRepository.getEmployeeIDByConductorId(operationReport.employeeID.toInt())

                    // Загружаем связанные элементы корзины
                    val cartItems = cartItemRepository
                        .getCartItemReportsByOperationId(operationId)
                        .first()

                    // Создаём обновлённый CartOperationReport
                    operationReport.copy(
                        employeeID = employeeID,
                        items = cartItems
                    )
                }

            // Формируем TripReport
            val tripReport = TripReport(
                routeID = routeID,
                startTime = startTime,
                endTime = endTime,
                carriageID = carriageID,
                carts = cartOperationReports
            )

            // Сохраняем отчёт
            val fileName = "trip_report_${System.currentTimeMillis()}.json"
            tripReportRepository.saveTripReport(tripReport, fileName)

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
