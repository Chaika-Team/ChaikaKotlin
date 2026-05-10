package com.chaikasoft.app.domain.sealed

import com.chaikasoft.app.domain.common.AppError

sealed interface RefreshStationsResult {
    /** Station refresh skipped because there is an active shift. */
    data object SkippedActiveShift : RefreshStationsResult

    /** Station refresh skipped because local cache is still fresh by TTL. */
    data object SkippedFreshCache : RefreshStationsResult

    /** Stations fetched and successfully saved to local database. */
    data class Success(val stationCount: Int) : RefreshStationsResult

    /** Remote/network layer returned an error. */
    data class RemoteFailure(val error: AppError) : RefreshStationsResult

    /** Local DB write failed. */
    data class LocalFailure(val cause: Exception) : RefreshStationsResult
}
