package com.example.chaika.data.room.repo

import com.example.chaika.data.room.dao.ConductorDao
import com.example.chaika.data.room.mappers.toDomain
import com.example.chaika.data.room.mappers.toEntity
import com.example.chaika.domain.models.ConductorDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Репозиторий для работы с данными проводников.
 *
 * @param conductorDao DAO для работы с таблицей `conductors`.
 */
class RoomConductorRepository @Inject constructor(
    private val conductorDao: ConductorDao
) : RoomConductorRepositoryInterface {

    override fun getAllConductors(): Flow<List<ConductorDomain>> {
        return conductorDao.getAllConductors().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getConductorByEmployeeID(employeeID: String): ConductorDomain? {
        return conductorDao.getConductorByEmployeeID(employeeID)?.toDomain()
    }

    override suspend fun getEmployeeIDByConductorId(conductorId: Int): String {
        return conductorDao.getAllConductors()
            .first()
            .find { it.id == conductorId }?.employeeID
            ?: throw IllegalArgumentException("Conductor not found for id: $conductorId")
    }

    override suspend fun insertConductor(conductorDomain: ConductorDomain) {
        conductorDao.insertConductor(conductorDomain.toEntity())
    }

    override suspend fun updateConductor(conductorDomain: ConductorDomain) {
        conductorDao.updateConductor(conductorDomain.toEntity())
    }

    override suspend fun deleteConductor(conductorDomain: ConductorDomain) {
        conductorDao.deleteConductor(conductorDomain.toEntity())
    }

    override suspend fun deleteAllConductors() {
        conductorDao.deleteAllConductors()
    }
}
