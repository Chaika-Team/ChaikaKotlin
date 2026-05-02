package com.chaikasoft.app.e2e.di

import com.chaikasoft.app.di.TripGateStartupModule
import com.chaikasoft.app.e2e.config.E2ETestArguments
import com.chaikasoft.app.startup.DisabledTripGateStartupSeam
import com.chaikasoft.app.startup.FakeSuccessTripGateStartupSeam
import com.chaikasoft.app.startup.NormalTripGateStartupSeam
import com.chaikasoft.app.startup.TripGateStartupMode
import com.chaikasoft.app.startup.TripGateStartupSeam
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Named
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [TripGateStartupModule::class],
)
object E2ETripGateStartupModule {

    @Provides
    @Singleton
    @Named("tripGateStartupMode")
    fun provideTripGateStartupMode(): TripGateStartupMode = E2ETestArguments.tripGateMode()

    @Provides
    @Singleton
    fun provideTripGateStartupSeam(
        @Named("tripGateStartupMode") mode: TripGateStartupMode,
        normal: NormalTripGateStartupSeam,
        disabled: DisabledTripGateStartupSeam,
        fakeSuccess: FakeSuccessTripGateStartupSeam,
    ): TripGateStartupSeam {
        return when (mode) {
            TripGateStartupMode.NORMAL -> normal
            TripGateStartupMode.DISABLED -> disabled
            TripGateStartupMode.FAKE_SUCCESS -> fakeSuccess
        }
    }
}
