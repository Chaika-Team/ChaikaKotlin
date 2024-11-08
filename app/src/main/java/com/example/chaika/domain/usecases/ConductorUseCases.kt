package com.example.chaika.domain.usecases

import com.example.chaika.data.room.repo.RoomConductorRepositoryInterface
import com.example.chaika.domain.models.ConductorDomain
import javax.inject.Inject

// Юзкейс для добавления проводника в базу данных
class AddConductorUseCase @Inject constructor(
    private val roomConductorRepositoryInterface: RoomConductorRepositoryInterface
) {
    suspend operator fun invoke(conductorDomain: ConductorDomain) {
        roomConductorRepositoryInterface.insertConductor(conductorDomain)
    }
}

// Юзкейс для удаления проводника из базы данных
class DeleteConductorUseCase @Inject constructor(
    private val roomConductorRepositoryInterface: RoomConductorRepositoryInterface
) {
    suspend operator fun invoke(conductorDomain: ConductorDomain) {
        roomConductorRepositoryInterface.deleteConductor(conductorDomain)
    }
}

// Тестовый Юзкейс для предзаполнения бд проводниками
class PrepopulateConductorsUseCase @Inject constructor(
    private val roomConductorRepositoryInterface: RoomConductorRepositoryInterface
) {
    suspend operator fun invoke() {
        val conductorDomains = listOf(
            ConductorDomain(id = 1, name = "Иван Иванов", image = "image_ivan"),
            ConductorDomain(id = 2, name = "Анна Смирнова", image = "image_anna")
        )
        conductorDomains.forEach { conductor ->
            roomConductorRepositoryInterface.insertConductor(conductor)
        }
    }
}