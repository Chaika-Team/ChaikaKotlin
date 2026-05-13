package com.chaikasoft.app.e2e.config

import androidx.test.platform.app.InstrumentationRegistry

enum class AuthBootstrapMode {
    DISABLED,
    FAKE_SUCCESS
}

object E2ETestArguments {
    private const val AUTH_MODE_ARG = "e2e.authMode"
    private const val ENV_MODE_ARG = "e2e.env"

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
}
