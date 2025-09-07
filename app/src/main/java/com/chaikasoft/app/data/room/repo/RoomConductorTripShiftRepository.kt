// File: com.chaikasoft.app/data/room/repository/RoomConductorTripShiftRepository.kt
package com.chaikasoft.app.data.room.repo

import com.chaikasoft.app.data.room.dao.ConductorTripShiftDao
import com.chaikasoft.app.data.room.mappers.toDomain
import com.chaikasoft.app.data.room.mappers.toEntity
import com.chaikasoft.app.domain.models.trip.ConductorTripShiftDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomConductorTripShiftRepository @Inject constructor(
    private val dao: ConductorTripShiftDao
) : RoomConductorTripShiftRepositoryInterface {

    override suspend fun insertOrUpdate(shift: ConductorTripShiftDomain) {
        dao.insertOrUpdate(shift.toEntity())
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
        dao.getActiveShift()?.toDomain()

    override fun observeActiveShift(): Flow<ConductorTripShiftDomain?> =
        dao.getActiveShiftFlow().map { it?.toDomain() }


    override fun observeAllShifts(): Flow<List<ConductorTripShiftDomain>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }
}
