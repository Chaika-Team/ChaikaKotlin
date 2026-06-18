package com.chaikasoft.app.data.room.repo

import android.util.Log
import androidx.room.withTransaction
import com.chaikasoft.app.data.room.AppDatabase
import com.chaikasoft.app.data.room.dao.CartOperationDao
import com.chaikasoft.app.data.room.dao.ConductorTripShiftDao
import com.chaikasoft.app.data.room.mappers.toDomain
import com.chaikasoft.app.data.room.mappers.toEntity
import com.chaikasoft.app.data.room.mappers.toTripShiftStatusDomain
import com.chaikasoft.app.domain.models.trip.ConductorTripShiftDomain
import com.chaikasoft.app.domain.models.trip.TripShiftStatusDomain
import com.chaikasoft.app.domain.sealed.StartShiftResult
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomConductorTripShiftRepository @Inject constructor(
    private val db: AppDatabase,
    private val dao: ConductorTripShiftDao,
    private val cartOperationDao: CartOperationDao
) : RoomConductorTripShiftRepositoryInterface {

    /**
     * Создаёт новую ACTIVE-смену.
     *
     * Ожидаемые конфликты уникальности превращаются в типизированные результаты, чтобы
     * доменный слой не работал с SQLite-исключением как с пользовательским сценарием.
     */
    override suspend fun tryStartNewShift(shift: ConductorTripShiftDomain): StartShiftResult {
        val entity = shift.toEntity()
        return db.withTransaction {
            try {
                dao.insertNew(entity)
                StartShiftResult.Started
            } catch (e: android.database.sqlite.SQLiteConstraintException) {
                Log.w(
                    TAG,
                    "Cannot start new shift! Error: ${e.message}",
                    e
                )
                when {
                    dao.getActiveShiftWithStations(TripShiftStatusDomain.ACTIVE.code) != null ->
                        StartShiftResult.ActiveShiftAlreadyExists
                    dao.getByUuid(entity.uuid) != null -> StartShiftResult.TripAlreadyRegistered
                    else -> throw e
                }
            }
        }
    }

    override suspend fun getShiftByUuid(uuid: String): ConductorTripShiftDomain? =
        dao.getByUuidWithStations(uuid)?.toDomain()

    override suspend fun deleteActiveShift(uuid: String, clearOperations: Boolean) {
        db.withTransaction {
            val shift = dao.getByUuid(uuid)
                ?: throw IllegalStateException("Shift not found: $uuid")
            check(shift.status == TripShiftStatusDomain.ACTIVE.code) {
                "Shift uuid=$uuid is not ACTIVE"
            }
            check(dao.deleteShift(shift) == 1) {
                "Shift uuid=$uuid was not deleted"
            }
            if (clearOperations) {
                cartOperationDao.clearAllOperations()
            }
        }
    }

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

    private companion object {
        const val TAG = "RoomConductorTripShiftRepository"
    }
}
