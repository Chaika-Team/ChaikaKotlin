package com.example.chaika.usecases

import com.example.chaika.dataBase.entities.Trip
import com.example.chaika.dataBase.models.ActionRepository
import com.example.chaika.dataBase.models.TripRepository
import kotlinx.coroutines.flow.Flow

class GetAllTripsUseCase(private val repository: TripRepository) {
    fun execute(): Flow<List<Trip>> {
        return repository.getAllTrips()
    }
}

class InsertTripUseCase(private val repository: TripRepository) {
    suspend fun execute(trip: Trip) {
        repository.insertTrip(trip)
    }
}

class DeleteTripAndActionsUseCase(
    private val tripRepository: TripRepository,
    private val actionRepository: ActionRepository
) {
    suspend fun execute(trip: Trip) {
        // Сначала удаляем все записи в таблице Actions, связанные с данной поездкой
        actionRepository.deleteActionsByTripId(trip.id)
        // Затем удаляем саму поездку
        tripRepository.deleteTrip(trip)
    }
}

class UpdateTripUseCase(private val repository: TripRepository) {
    suspend fun execute(trip: Trip) {
        repository.updateTrip(trip)
    }
}

class FilterTripsUseCase {
    fun execute(allTrips: List<Trip>, query: String): List<Trip> {
        return if (query.isEmpty()) {
            allTrips
        } else {
            allTrips.filter { trip ->
                trip.name.lowercase().contains(query.lowercase())
            }
        }
    }
}
