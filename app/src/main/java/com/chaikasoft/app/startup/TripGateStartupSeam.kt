package com.chaikasoft.app.startup

import android.util.Log
import com.chaikasoft.app.domain.sealed.RefreshStationsResult
import com.chaikasoft.app.domain.usecases.RefreshStationsOnLaunchUseCase
import javax.inject.Inject

enum class TripGateStartupMode {
    NORMAL,
    DISABLED,
    FAKE_SUCCESS
}

data class TripGateStartupOutcome(val hadRefreshFailure: Boolean)

interface TripGateStartupSeam {
    suspend fun prepareForTripEntry(): TripGateStartupOutcome
}

class NormalTripGateStartupSeam @Inject constructor(
    private val refreshStationsOnLaunchUseCase: RefreshStationsOnLaunchUseCase
) : TripGateStartupSeam {

    override suspend fun prepareForTripEntry(): TripGateStartupOutcome {
        val hadRefreshFailure = when (val result = refreshStationsOnLaunchUseCase()) {
            is RefreshStationsResult.Success -> {
                Log.i(TAG, "Stations refresh success: count=${result.stationCount}")
                false
            }
            is RefreshStationsResult.SkippedActiveShift -> {
                Log.i(TAG, "Stations refresh skipped: active shift")
                false
            }
            is RefreshStationsResult.SkippedFreshCache -> {
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

        return TripGateStartupOutcome(hadRefreshFailure = hadRefreshFailure)
    }

    private companion object {
        const val TAG = "TripGateStartupSeam"
    }
}

class DisabledTripGateStartupSeam @Inject constructor() : TripGateStartupSeam {
    override suspend fun prepareForTripEntry(): TripGateStartupOutcome =
        TripGateStartupOutcome(hadRefreshFailure = false)
}

class FakeSuccessTripGateStartupSeam @Inject constructor() : TripGateStartupSeam {
    override suspend fun prepareForTripEntry(): TripGateStartupOutcome =
        TripGateStartupOutcome(hadRefreshFailure = false)
}
