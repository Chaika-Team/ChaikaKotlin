package com.chaikasoft.app.data.dataSource.repo

import com.chaikasoft.app.data.dataSource.apiService.ChaikaSoftApiService
import com.chaikasoft.app.data.dataSource.mappers.*
import com.chaikasoft.app.domain.models.trip.CarriageDomain
import com.chaikasoft.app.domain.models.trip.StationDomain
import com.chaikasoft.app.domain.models.trip.TripDomain
import javax.inject.Inject

class ChaikaSoftRoutesRepository @Inject constructor(
    private val api: ChaikaSoftApiService
) : ChaikaRoutesAdapterApiServiceRepositoryInterface {

    override suspend fun suggestStations(query: String, limit: Int): List<StationDomain> {
        val body = api.findStations(query, limit)
        return body.stations.map { it.toDomain() }
    }

    override suspend fun searchTripsByRoute(date: String, trainNumber: String): List<TripDomain> {
        val body = api.findTrips(date = date, trainNumber = trainNumber)
        return body.trips.map { it.toDomain() }
    }

    override suspend fun searchTripsByStations(
        date: String,
        fromCode: Int,
        toCode: Int
    ): List<TripDomain> {
        val body = api.findTrips(date = date, fromCode = fromCode, toCode = toCode)
        return body.trips.map { it.toDomain() }
    }

    override suspend fun getCarriagesForTrain(tripUuid: String): List<CarriageDomain> {
        val body = api.getCarsForTrip(tripUuid)
        return body.cars.map { it.toDomain() }
    }

    override suspend fun fetchAllStations(limit: Int): List<StationDomain> {
        val body = api.findStations(query = null, limit = limit)
        return body.stations.map { it.toDomain() }
    }
}
