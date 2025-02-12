package com.example.chaika.data.room.repo

import com.example.chaika.domain.models.ConductorDomain
import kotlinx.coroutines.flow.Flow

interface RoomConductorRepositoryInterface {
    fun getAllConductors(): Flow<List<ConductorDomain>>
    suspend fun getConductorByEmployeeID(employeeID: String): ConductorDomain?
    suspend fun getEmployeeIDByConductorId(conductorId: Int): String
    suspend fun insertConductor(conductorDomain: ConductorDomain)
    suspend fun updateConductor(conductorDomain: ConductorDomain)
    suspend fun deleteConductor(conductorDomain: ConductorDomain)
}

