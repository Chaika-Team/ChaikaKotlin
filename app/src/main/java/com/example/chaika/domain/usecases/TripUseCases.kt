package com.example.chaika.domain.usecases

import com.example.chaika.data.room.entities.Trip
import com.example.chaika.data.room.repo.ActionRepository
import com.example.chaika.data.room.repo.TripRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllTripsUseCase @Inject constructor(
    private val repository: TripRepository
) {
    fun execute(): Flow<List<Trip>> {
        return repository.getAllTrips()
    }
}

class InsertTripUseCase @Inject constructor(
    private val repository: TripRepository
) {
    suspend fun execute(trip: Trip) {
        repository.insertTrip(trip)
    }
}

class DeleteTripAndActionsUseCase @Inject constructor(
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

class UpdateTripUseCase @Inject constructor(
    private val repository: TripRepository
) {
    suspend fun execute(trip: Trip) {
        repository.updateTrip(trip)
    }
}

class FilterTripsUseCase @Inject constructor() {
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
