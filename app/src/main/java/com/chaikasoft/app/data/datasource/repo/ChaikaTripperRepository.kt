package com.chaikasoft.app.data.datasource.repo

import com.chaikasoft.app.data.datasource.apiservice.ChaikaSoftApiService
import com.chaikasoft.app.data.datasource.common.remoteCall
import com.chaikasoft.app.data.datasource.mappers.toDomain
import com.chaikasoft.app.domain.common.ErrorReporter
import com.chaikasoft.app.domain.common.RemoteResult
import com.chaikasoft.app.domain.models.trip.StationDomain
import com.chaikasoft.app.domain.models.trip.TripDomain
import javax.inject.Inject

class ChaikaTripperRepository @Inject constructor(
    private val api: ChaikaSoftApiService,
    private val errorReporter: ErrorReporter = ErrorReporter.NoOp
) : ChaikaTripperRepositoryInterface {

    override suspend fun searchTripsByStations(
        date: String,
        fromCode: String,
        toCode: String
    ): RemoteResult<List<TripDomain>> = remoteCall(errorReporter) {
        val body = api.findTrips(date = date, fromCode = fromCode, toCode = toCode)
        body.trips.orEmpty().map { it.toDomain() }
    }

    override suspend fun fetchAllStations(limit: Int): RemoteResult<List<StationDomain>> =
        remoteCall(errorReporter) {
            val body = api.findStations(limit = limit)
            body.stations.orEmpty().map { it.toDomain() }
        }
}
