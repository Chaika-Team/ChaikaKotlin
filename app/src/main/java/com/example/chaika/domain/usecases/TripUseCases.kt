package com.example.chaika.domain.usecases

import com.example.chaika.data.dataSource.repo.ChaikaRoutesAdapterApiServiceRepositoryInterface
import com.example.chaika.domain.models.trip.CarriageDomain
import com.example.chaika.domain.models.trip.StationDomain
import com.example.chaika.domain.models.trip.TripDomain
import javax.inject.Inject

class SuggestStationsUseCase @Inject constructor(
    private val repository: ChaikaRoutesAdapterApiServiceRepositoryInterface
) {
    suspend operator fun invoke(query: String, limit: Int = 10): List<StationDomain> =
        repository.suggestStations(query, limit)
}


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