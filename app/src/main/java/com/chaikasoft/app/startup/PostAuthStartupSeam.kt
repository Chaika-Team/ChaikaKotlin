package com.chaikasoft.app.startup

import android.util.Log
import com.chaikasoft.app.domain.sealed.RefreshProductsResult
import com.chaikasoft.app.domain.sealed.RefreshStationsResult
import com.chaikasoft.app.domain.usecases.RefreshProductsOnLaunchUseCase
import com.chaikasoft.app.domain.usecases.RefreshStationsOnLaunchUseCase
import javax.inject.Inject

enum class PostAuthStartupMode {
    NORMAL,
    DISABLED,
    FAKE_SUCCESS
}

data class PostAuthStartupOutcome(val hadRefreshFailure: Boolean)

interface PostAuthStartupSeam {
    suspend fun prepareForAuthenticatedApp(): PostAuthStartupOutcome
}

class NormalPostAuthStartupSeam @Inject constructor(
    private val coordinator: PostAuthStartupCoordinator
) : PostAuthStartupSeam {
    override suspend fun prepareForAuthenticatedApp(): PostAuthStartupOutcome =
        coordinator.prepare()
}

class PostAuthStartupCoordinator @Inject constructor(
    private val refreshStationsOnLaunchUseCase: RefreshStationsOnLaunchUseCase,
    private val refreshProductsOnLaunchUseCase: RefreshProductsOnLaunchUseCase
) {
    suspend fun prepare(): PostAuthStartupOutcome {
        val stationsHadFailure = refreshStations()
        val productsHadFailure = refreshProducts()

        return PostAuthStartupOutcome(
            hadRefreshFailure = stationsHadFailure || productsHadFailure
        )
    }

    private suspend fun refreshStations(): Boolean =
        when (val result = refreshStationsOnLaunchUseCase()) {
            is RefreshStationsResult.Success -> {
                Log.i(TAG, "Stations refresh success: count=${result.stationCount}")
                false
            }
            RefreshStationsResult.SkippedActiveShift -> {
                Log.i(TAG, "Stations refresh skipped: active shift")
                false
            }
            RefreshStationsResult.SkippedFreshCache -> {
                Log.i(TAG, "Stations refresh skipped: fresh cache")
                false
            }
            is RefreshStationsResult.RemoteFailure -> {
                Log.w(TAG, "Stations refresh remote failure: ${result.error}")
                true
            }
            is RefreshStationsResult.LocalFailure -> {
                Log.e(TAG, "Stations refresh local failure: ${result.cause.message}", result.cause)
                true
            }
        }

    private suspend fun refreshProducts(): Boolean =
        when (val result = refreshProductsOnLaunchUseCase()) {
            is RefreshProductsResult.Success -> {
                Log.i(TAG, "Products refresh success: count=${result.productCount}")
                false
            }
            RefreshProductsResult.SkippedActiveShift -> {
                Log.i(TAG, "Products refresh skipped: active shift")
                false
            }
            RefreshProductsResult.SkippedFreshCache -> {
                Log.i(TAG, "Products refresh skipped: fresh cache")
                false
            }
            is RefreshProductsResult.RemoteFailure -> {
                Log.w(TAG, "Products refresh remote failure: ${result.error}")
                true
            }
            is RefreshProductsResult.LocalFailure -> {
                Log.e(TAG, "Products refresh local failure: ${result.cause.message}", result.cause)
                true
            }
        }

    private companion object {
        const val TAG = "PostAuthStartup"
    }
}

class DisabledPostAuthStartupSeam @Inject constructor() : PostAuthStartupSeam {
    override suspend fun prepareForAuthenticatedApp(): PostAuthStartupOutcome =
        PostAuthStartupOutcome(hadRefreshFailure = false)
}

class FakeSuccessPostAuthStartupSeam @Inject constructor() : PostAuthStartupSeam {
    override suspend fun prepareForAuthenticatedApp(): PostAuthStartupOutcome =
        PostAuthStartupOutcome(hadRefreshFailure = false)
}
