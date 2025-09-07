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
        val resp = api.findStations(query, limit)
        if (resp.isSuccessful) {
            return resp.body()?.stations?.map { it.toDomain() } ?: emptyList()
        }
        throw Exception("Error suggesting stations: \${resp.code()} ${'$'}{resp.message()}")
    }

    override suspend fun searchTripsByRoute(date: String, trainNumber: String): List<TripDomain> {
        val resp = api.findTrips(date = date, trainNumber = trainNumber)
        if (resp.isSuccessful) {
            return resp.body()?.trips?.map { it.toDomain() } ?: emptyList()
        }
        throw Exception("Error searching trips by route: \${resp.code()} ${'$'}{resp.message()}")
    }

    override suspend fun searchTripsByStations(
        date: String,
        fromCode: Int,
        toCode: Int
    ): List<TripDomain> {
        val resp = api.findTrips(date = date, fromCode = fromCode, toCode = toCode)
        if (resp.isSuccessful) {
            return resp.body()?.trips?.map { it.toDomain() } ?: emptyList()
        }
        throw Exception("Error searching trips by stations: \${resp.code()} ${'$'}{resp.message()}")
    }

    override suspend fun getCarriagesForTrain(tripUuid: String): List<CarriageDomain> {
        val resp = api.getCarsForTrip(tripUuid)
        if (resp.isSuccessful) {
            return resp.body()?.cars?.map { it.toDomain() } ?: emptyList()
        }
        throw Exception("Error fetching cars for trip: \${resp.code()} ${'$'}{resp.message()}")
    }
}
