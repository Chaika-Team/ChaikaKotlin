package com.example.chaika.data.room.repo


import com.example.chaika.data.room.dao.ConductorDao
import com.example.chaika.data.room.mappers.toDomain
import com.example.chaika.data.room.mappers.toEntity
import com.example.chaika.domain.models.ConductorDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomConductorRepository @Inject constructor(private val conductorDao: ConductorDao) :
    RoomConductorRepositoryInterface {

    override fun getAllConductors(): Flow<List<ConductorDomain>> {
        return conductorDao.getAllConductors().map { list -> list.map { it.toDomain() } }
    }

//    suspend fun getConductorById(id: Int): ConductorDomain? {
//        return conductorDao.getConductorById(id)?.toDomain()
//    }

    override suspend fun insertConductor(conductorDomain: ConductorDomain) {
        conductorDao.insertConductor(conductorDomain.toEntity())
    }

    override suspend fun updateConductor(conductorDomain: ConductorDomain) {
        conductorDao.updateConductor(conductorDomain.toEntity())
    }

    override suspend fun deleteConductor(conductorDomain: ConductorDomain) {
        conductorDao.deleteConductor(conductorDomain.toEntity())
    }
}
