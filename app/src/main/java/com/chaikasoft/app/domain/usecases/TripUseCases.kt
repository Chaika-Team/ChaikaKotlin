package com.chaikasoft.app.domain.usecases

import com.chaikasoft.app.data.dataSource.repo.ChaikaRoutesAdapterApiServiceRepositoryInterface
import com.chaikasoft.app.domain.models.trip.CarriageDomain
import com.chaikasoft.app.domain.models.trip.TripDomain
import javax.inject.Inject

class SearchTripsByRouteUseCase @Inject constructor(
    private val repository: ChaikaRoutesAdapterApiServiceRepositoryInterface
) {
    suspend operator fun invoke(date: String, trainNumber: String): List<TripDomain> =
        repository.searchTripsByRoute(date, trainNumber)
}


class SearchTripsByStationsUseCase @Inject constructor(
    private val repository: ChaikaRoutesAdapterApiServiceRepositoryInterface
) {
    suspend operator fun invoke(date: String, fromCode: Int, toCode: Int): List<TripDomain> =
        repository.searchTripsByStations(date, fromCode, toCode)
}


class GetCarriagesForTrainUseCase @Inject constructor(
    private val repository: ChaikaRoutesAdapterApiServiceRepositoryInterface
) {
    suspend operator fun invoke(tripUuid: String): List<CarriageDomain> =
        repository.getCarriagesForTrain(tripUuid)
}