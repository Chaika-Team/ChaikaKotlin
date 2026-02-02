package com.chaikasoft.app.data.dataSource.repo

import android.util.Log
import com.chaikasoft.app.data.dataSource.apiService.ChaikaSoftApiService
import com.chaikasoft.app.data.dataSource.mappers.*
import com.chaikasoft.app.domain.models.trip.CarriageDomain
import com.chaikasoft.app.domain.models.trip.StationDomain
import com.chaikasoft.app.domain.models.trip.TripDomain
import javax.inject.Inject

class ChaikaSoftRoutesRepository @Inject constructor(
    private val api: ChaikaSoftApiService
) : ChaikaRoutesAdapterApiServiceRepositoryInterface {


    override suspend fun searchTripsByRoute(date: String,): List<TripDomain> {
        val body = api.findTrips(date = date)
        return body.trips.map { it.toDomain() }
    }

    override suspend fun searchTripsByStations(
        date: String,
        fromCode: String,
        toCode: String
    ): List<TripDomain> {
        val body = api.findTrips(date = date, fromCode = fromCode, toCode = toCode)
        return body.trips.map { it.toDomain() }
    }

    override suspend fun getCarriagesForTrain(tripUuid: String): List<CarriageDomain> {
        val body = api.getCarsForTrip(tripUuid)
        return body.cars.map { it.toDomain() }
    }

    override suspend fun fetchAllStations(limit: Int): List<StationDomain> {
        val body = api.findStations(limit = limit)
        return body.stations.map { it.toDomain() }
    }
}
