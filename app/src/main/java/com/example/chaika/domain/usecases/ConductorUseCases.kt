package com.example.chaika.domain.usecases


import com.example.chaika.data.room.repo.ConductorRepository
import com.example.chaika.domain.models.Conductor
import javax.inject.Inject

// Юзкейс для добавления проводника в базу данных
class AddConductorUseCase @Inject constructor(
    private val conductorRepository: ConductorRepository
) {
    suspend operator fun invoke(conductor: Conductor) {
        conductorRepository.insertConductor(conductor)
    }
}

// Юзкейс для удаления проводника из базы данных
class DeleteConductorUseCase @Inject constructor(
    private val conductorRepository: ConductorRepository
) {
    suspend operator fun invoke(conductor: Conductor) {
        conductorRepository.deleteConductor(conductor)
    }
}
