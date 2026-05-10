package com.chaikasoft.app.e2e.config

import androidx.test.platform.app.InstrumentationRegistry
import com.chaikasoft.app.startup.TripGateStartupMode

enum class AuthBootstrapMode {
    DISABLED,
    FAKE_SUCCESS
}

object E2ETestArguments {
    private const val AUTH_MODE_ARG = "e2e.authMode"
    private const val ENV_MODE_ARG = "e2e.env"
    private const val TRIP_GATE_MODE_ARG = "e2e.tripGateMode"

    fun authMode(): AuthBootstrapMode {
        val mode = InstrumentationRegistry.getArguments()
            .getString(AUTH_MODE_ARG, "fake_success")
            ?.lowercase()
            .orEmpty()

        return when (mode) {
            "disabled" -> AuthBootstrapMode.DISABLED
            else -> AuthBootstrapMode.FAKE_SUCCESS
        }
    }

    fun isEnvMode(): Boolean {
        return InstrumentationRegistry.getArguments()
            .getString(ENV_MODE_ARG, "false")
            ?.toBooleanStrictOrNull()
            ?: false
    }

    fun tripGateMode(): TripGateStartupMode {
        val mode = InstrumentationRegistry.getArguments()
            .getString(TRIP_GATE_MODE_ARG)
            ?.lowercase()
            ?.trim()

        if (mode.isNullOrEmpty()) {
            return if (isEnvMode()) {
                TripGateStartupMode.NORMAL
            } else {
                TripGateStartupMode.FAKE_SUCCESS
            }
        }

        return when (mode) {
            "normal" -> TripGateStartupMode.NORMAL
            "disabled" -> TripGateStartupMode.DISABLED
            else -> TripGateStartupMode.FAKE_SUCCESS
        }
    }
}
