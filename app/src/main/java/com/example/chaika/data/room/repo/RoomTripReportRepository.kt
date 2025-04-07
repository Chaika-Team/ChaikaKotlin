package com.example.chaika.data.room.repo

import com.example.chaika.data.room.dao.TripReportDao
import com.example.chaika.data.room.entities.TripReportEntity
import com.example.chaika.domain.models.TripReport
import com.example.chaika.domain.models.CartOperationReport
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import javax.inject.Inject

class RoomTripReportRepository @Inject constructor(
    private val tripReportDao: TripReportDao,
    private val moshi: Moshi // Moshi предоставляется через DI
) : RoomTripReportRepositoryInterface {

    // Настраиваем Moshi для работы со списком CartOperationReport.
    private val listType =
        Types.newParameterizedType(List::class.java, CartOperationReport::class.java)

    // Переименовано в cartsAdapter, поскольку в БД поле называется carts_json.
    private val cartsAdapter = moshi.adapter<List<CartOperationReport>>(listType)

    override suspend fun saveTripReport(report: TripReport): Long {
        // Сериализуем список операций, который теперь называется carts, в JSON
        val cartsJson = cartsAdapter.toJson(report.carts)
        val entity = TripReportEntity(
            routeID = report.routeID,
            startTime = report.startTime,
            endTime = report.endTime,
            carriageID = report.carriageID,
            cartsJson = cartsJson, // используем новое имя поля
            isSent = false
        )
        return tripReportDao.insertReport(entity)
    }

    override suspend fun getPendingTripReports(): List<Pair<Long, TripReport>> {
        val entities = tripReportDao.getPendingReports()
        return entities.map { entity ->
            // Десериализуем JSON из поля carts_json
            val carts = cartsAdapter.fromJson(entity.cartsJson) ?: emptyList()
            entity.id to TripReport(
                routeID = entity.routeID,
                startTime = entity.startTime,
                endTime = entity.endTime,
                carriageID = entity.carriageID,
                carts = carts
            )
        }
    }

    override suspend fun markReportAsSent(reportId: Long) {
        tripReportDao.updateReportStatus(reportId, true)
    }

    override suspend fun uploadTripReport(report: TripReport): Boolean {
        // Здесь эмулируется отправка отчёта на сервер.
        return true
    }
}
