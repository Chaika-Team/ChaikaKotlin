package com.example.chaika.domain.usecases

import com.example.chaika.data.room.repo.RoomConductorRepositoryInterface
import com.example.chaika.domain.models.ConductorDomain
import javax.inject.Inject

class AddConductorUseCase @Inject constructor(
    private val roomConductorRepositoryInterface: RoomConductorRepositoryInterface
) {
    suspend operator fun invoke(conductorDomain: ConductorDomain) {
        roomConductorRepositoryInterface.insertConductor(conductorDomain)
    }
}

class DeleteConductorUseCase @Inject constructor(
    private val roomConductorRepositoryInterface: RoomConductorRepositoryInterface
) {
    suspend operator fun invoke(conductorDomain: ConductorDomain) {
        roomConductorRepositoryInterface.deleteConductor(conductorDomain)
    }
}

class PrepopulateConductorsUseCase @Inject constructor(
    private val roomConductorRepositoryInterface: RoomConductorRepositoryInterface
) {
    suspend operator fun invoke() {
        val conductorDomains = listOf(
            ConductorDomain(
                id = 1,
                name = "Иван Иванов",
                employeeID = "EMP001",
                image = "image_ivan"
            ),
            ConductorDomain(
                id = 2,
                name = "Анна Смирнова",
                employeeID = "EMP002",
                image = "image_anna"
            )
        )
        conductorDomains.forEach { conductor ->
            roomConductorRepositoryInterface.insertConductor(conductor)
        }
    }
}
