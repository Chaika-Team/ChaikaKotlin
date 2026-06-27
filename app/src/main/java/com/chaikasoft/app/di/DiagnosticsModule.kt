package com.chaikasoft.app.di

import com.chaikasoft.app.BuildConfig
import com.chaikasoft.app.diagnostics.ErrorReporter
import com.chaikasoft.app.diagnostics.NoOpErrorReporter
import com.chaikasoft.app.diagnostics.SentryErrorReporter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DiagnosticsModule {
    @Provides
    @Singleton
    fun provideErrorReporter(
        noOpErrorReporter: NoOpErrorReporter,
        sentryErrorReporter: SentryErrorReporter
    ): ErrorReporter {
        val shouldUseSentry = BuildConfig.ERROR_REPORTING_ENABLED &&
            BuildConfig.GLITCHTIP_DSN.isNotBlank()
        return if (shouldUseSentry) {
            sentryErrorReporter
        } else {
            noOpErrorReporter
        }
    }
}
