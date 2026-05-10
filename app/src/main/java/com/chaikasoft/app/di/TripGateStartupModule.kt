package com.chaikasoft.app.di

import com.chaikasoft.app.startup.DisabledTripGateStartupSeam
import com.chaikasoft.app.startup.FakeSuccessTripGateStartupSeam
import com.chaikasoft.app.startup.NormalTripGateStartupSeam
import com.chaikasoft.app.startup.TripGateStartupMode
import com.chaikasoft.app.startup.TripGateStartupSeam
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TripGateStartupModule {

    @Provides
    @Singleton
    @Named("tripGateStartupMode")
    fun provideTripGateStartupMode(): TripGateStartupMode = TripGateStartupMode.NORMAL

    @Provides
    @Singleton
    fun provideTripGateStartupSeam(
        @Named("tripGateStartupMode") mode: TripGateStartupMode,
        normal: NormalTripGateStartupSeam,
        disabled: DisabledTripGateStartupSeam,
        fakeSuccess: FakeSuccessTripGateStartupSeam
    ): TripGateStartupSeam = when (mode) {
        TripGateStartupMode.NORMAL -> normal
        TripGateStartupMode.DISABLED -> disabled
        TripGateStartupMode.FAKE_SUCCESS -> fakeSuccess
    }
}
