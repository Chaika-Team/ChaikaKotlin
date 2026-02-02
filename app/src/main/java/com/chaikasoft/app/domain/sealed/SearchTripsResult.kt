package com.chaikasoft.app.domain.sealed

import com.chaikasoft.app.domain.common.AppError
import com.chaikasoft.app.domain.models.trip.TripDomain

sealed interface SearchTripsResult {
    data class Success(val trips: List<TripDomain>) : SearchTripsResult
    data class Failure(val error: AppError) : SearchTripsResult
}
