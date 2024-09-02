package com.example.chaika.data.room.repo


import com.example.chaika.data.room.dao.ConductorDao
import com.example.chaika.data.room.mappers.toDomain
import com.example.chaika.data.room.mappers.toEntity
import com.example.chaika.domain.models.Conductor

class ConductorRepository(private val conductorDao: ConductorDao) {

    suspend fun getAllConductors(): List<Conductor> {
        return conductorDao.getAllConductors().map { it.toDomain() }
    }

//    suspend fun getConductorById(id: Int): Conductor? {
//        return conductorDao.getConductorById(id)?.toDomain()
//    }

    suspend fun insertConductor(conductor: Conductor) {
        conductorDao.insertConductor(conductor.toEntity())
    }

    suspend fun updateConductor(conductor: Conductor) {
        conductorDao.updateConductor(conductor.toEntity())
    }

    suspend fun deleteConductor(conductor: Conductor) {
        conductorDao.deleteConductor(conductor.toEntity())
    }
}
