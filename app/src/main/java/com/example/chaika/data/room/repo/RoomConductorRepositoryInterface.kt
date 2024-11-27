package com.example.chaika.data.room.repo

import com.example.chaika.domain.models.ConductorDomain
import kotlinx.coroutines.flow.Flow

interface RoomConductorRepositoryInterface {
    fun getAllConductors(): Flow<List<ConductorDomain>>
    suspend fun getEmployeeIDByConductorId(conductorId: Int): String // Новый метод
    suspend fun insertConductor(conductorDomain: ConductorDomain)
    suspend fun updateConductor(conductorDomain: ConductorDomain)
    suspend fun deleteConductor(conductorDomain: ConductorDomain)
}
