package com.chaikasoft.app.e2e.di

import com.chaikasoft.app.auth.AuthSessionBootstrap
import com.chaikasoft.app.auth.NoOpAuthSessionBootstrap
import com.chaikasoft.app.di.AuthSessionBootstrapModule
import com.chaikasoft.app.e2e.config.AuthBootstrapMode
import com.chaikasoft.app.e2e.config.E2ETestArguments
import com.chaikasoft.app.e2e.seams.DeterministicAuthSessionBootstrap
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Named
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AuthSessionBootstrapModule::class],
)
object E2EAuthSessionBootstrapModule {

    @Provides
    @Singleton
    @Named("authBootstrapMode")
    fun provideAuthBootstrapMode(): AuthBootstrapMode = E2ETestArguments.authMode()

    @Provides
    @Singleton
    fun provideAuthSessionBootstrap(
        @Named("authBootstrapMode") mode: AuthBootstrapMode,
        deterministic: DeterministicAuthSessionBootstrap,
        noOp: NoOpAuthSessionBootstrap,
    ): AuthSessionBootstrap {
        return when (mode) {
            AuthBootstrapMode.DISABLED -> noOp
            AuthBootstrapMode.FAKE_SUCCESS -> deterministic
        }
    }
}
