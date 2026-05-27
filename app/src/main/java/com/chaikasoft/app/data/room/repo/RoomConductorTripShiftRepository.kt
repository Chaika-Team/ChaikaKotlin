package com.chaikasoft.app.data.room.repo

import android.util.Log
import com.chaikasoft.app.data.room.dao.ConductorTripShiftDao
import com.chaikasoft.app.data.room.mappers.toDomain
import com.chaikasoft.app.data.room.mappers.toEntity
import com.chaikasoft.app.data.room.mappers.toTripShiftStatusDomain
import com.chaikasoft.app.domain.models.trip.ConductorTripShiftDomain
import com.chaikasoft.app.domain.models.trip.TripShiftStatusDomain
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomConductorTripShiftRepository @Inject constructor(private val dao: ConductorTripShiftDao) :
    RoomConductorTripShiftRepositoryInterface {

    /**
     * Создаёт новую ACTIVE-смену.
     *
     * Конфликт уникальности активной смены превращается в false, чтобы доменный слой
     * не работал с SQLite-исключением как с обычным пользовательским сценарием.
     */
    override suspend fun tryStartNewShift(shift: ConductorTripShiftDomain): Boolean {
        val entity = shift.toEntity()
        return try {
            dao.insertNew(entity)
            true
        } catch (e: android.database.sqlite.SQLiteConstraintException) {
            Log.w(
                "RoomConductorTripShiftRepository",
                "Cannot start new shift! Error: ${e.message}",
                e
            )
            false
        }
    }

    override suspend fun getShiftByUuid(uuid: String): ConductorTripShiftDomain? =
        dao.getByUuidWithStations(uuid)?.toDomain()

    override suspend fun updateStatusAndReport(
        uuid: String,
        newStatus: Int,
        reportJson: String?,
        updatedAt: Long
    ) {
        dao.updateStatusAndReport(uuid, newStatus, reportJson, updatedAt)
    }

    override suspend fun getActiveShift(): ConductorTripShiftDomain? =
        dao.getActiveShiftWithStations(TripShiftStatusDomain.ACTIVE.code)?.toDomain()

    override fun observeActiveShift(): Flow<ConductorTripShiftDomain?> =
        dao.getActiveShiftWithStationsFlow(TripShiftStatusDomain.ACTIVE.code).map { it?.toDomain() }

    override fun observeShiftHistory(): Flow<List<ConductorTripShiftDomain>> =
        dao.getHistoryWithStations().map { list -> list.map { it.toDomain() } }

    override suspend fun getStatusAndReport(uuid: String): Pair<TripShiftStatusDomain, String?> {
        val e = dao.getByUuid(uuid)
            ?: throw IllegalStateException("Shift not found: $uuid")
        return e.status.toTripShiftStatusDomain() to e.report
    }
}
