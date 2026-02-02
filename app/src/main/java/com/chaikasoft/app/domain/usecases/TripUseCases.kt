package com.chaikasoft.app.domain.usecases

import com.chaikasoft.app.data.dataSource.repo.ChaikaTripperRepositoryInterface
import com.chaikasoft.app.domain.common.RemoteResult
import com.chaikasoft.app.domain.models.trip.CarriageDomain
import com.chaikasoft.app.domain.models.trip.TripDomain
import com.chaikasoft.app.domain.sealed.SearchTripsResult
import javax.inject.Inject

@Deprecated("No API for now")
class SearchTripsByRouteUseCase @Inject constructor(
    private val repository: ChaikaTripperRepositoryInterface
) {
    suspend operator fun invoke(date: String): List<TripDomain> =
        repository.searchTripsByRoute(date)
}

class SearchTripsByStationsUseCase @Inject constructor(
    private val repository: ChaikaTripperRepositoryInterface
) {
    suspend operator fun invoke(date: String, fromCode: String, toCode: String): SearchTripsResult {
        return when (val res = repository.searchTripsByStations(date, fromCode, toCode)) {
            is RemoteResult.Success -> SearchTripsResult.Success(res.data)
            is RemoteResult.Failure -> SearchTripsResult.Failure(res.error)
        }
    }
}
@Deprecated("Should be replaced to manual input")
class GetCarriagesForTrainUseCase @Inject constructor(
    private val repository: ChaikaTripperRepositoryInterface
) {
    suspend operator fun invoke(tripUuid: String): List<CarriageDomain> =
        repository.getCarriagesForTrain(tripUuid)
}