// File: com/example/chaika/data/room/repository/RoomConductorTripShiftRepository.kt
package com.example.chaika.data.room.repo

import com.example.chaika.data.room.dao.ConductorTripShiftDao
import com.example.chaika.data.room.mappers.toDomain
import com.example.chaika.data.room.mappers.toEntity
import com.example.chaika.domain.models.trip.ConductorTripShiftDomain
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
