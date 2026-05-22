package com.chaikasoft.app.domain.sealed

import com.chaikasoft.app.domain.common.AppError

sealed interface RefreshProductsResult {
    /** Product refresh skipped because local cache is still fresh by TTL. */
    data object SkippedFreshCache : RefreshProductsResult

    /** Products fetched and local cache is up to date. */
    data class Success(val productCount: Int) : RefreshProductsResult

    /** Remote/network layer returned an error. */
    data class RemoteFailure(val error: AppError) : RefreshProductsResult

    /** Local DB write failed. */
    data class LocalFailure(val cause: Exception) : RefreshProductsResult
}
