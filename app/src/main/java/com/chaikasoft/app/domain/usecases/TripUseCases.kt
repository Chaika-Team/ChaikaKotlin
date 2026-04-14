package com.chaikasoft.app.domain.usecases

import com.chaikasoft.app.data.datasource.repo.ChaikaTripperRepositoryInterface
import com.chaikasoft.app.domain.common.RemoteResult
import com.chaikasoft.app.domain.sealed.SearchTripsResult
import javax.inject.Inject

class SearchTripsByStationsUseCase @Inject constructor(
    private val repository: ChaikaTripperRepositoryInterface
) {
    suspend operator fun invoke(date: String, fromCode: String, toCode: String): SearchTripsResult =
        when (val res = repository.searchTripsByStations(date, fromCode, toCode)) {
            is RemoteResult.Success -> SearchTripsResult.Success(res.data)
            is RemoteResult.Failure -> SearchTripsResult.Failure(res.error)
        }
}
