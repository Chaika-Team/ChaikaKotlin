package com.chaikasoft.app.domain.usecases

import com.chaikasoft.app.data.dataSource.repo.ChaikaRoutesAdapterApiServiceRepositoryInterface
import com.chaikasoft.app.domain.models.trip.CarriageDomain
import com.chaikasoft.app.domain.models.trip.TripDomain
import javax.inject.Inject

@Deprecated("No API for now")
class SearchTripsByRouteUseCase @Inject constructor(
    private val repository: ChaikaRoutesAdapterApiServiceRepositoryInterface
) {
    suspend operator fun invoke(date: String): List<TripDomain> =
        repository.searchTripsByRoute(date)
}

class SearchTripsByStationsUseCase @Inject constructor(
    private val repository: ChaikaRoutesAdapterApiServiceRepositoryInterface
) {
    suspend operator fun invoke(date: String, fromCode: String, toCode: String): List<TripDomain> =
        repository.searchTripsByStations(date, fromCode, toCode)
}

@Deprecated("Should be replaced to manual input")
class GetCarriagesForTrainUseCase @Inject constructor(
    private val repository: ChaikaRoutesAdapterApiServiceRepositoryInterface
) {
    suspend operator fun invoke(tripUuid: String): List<CarriageDomain> =
        repository.getCarriagesForTrain(tripUuid)
}