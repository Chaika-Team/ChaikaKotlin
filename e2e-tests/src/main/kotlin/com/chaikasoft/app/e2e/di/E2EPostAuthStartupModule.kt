package com.chaikasoft.app.e2e.di

import com.chaikasoft.app.di.PostAuthStartupModule
import com.chaikasoft.app.e2e.config.E2ETestArguments
import com.chaikasoft.app.startup.DisabledPostAuthStartupSeam
import com.chaikasoft.app.startup.FakeSuccessPostAuthStartupSeam
import com.chaikasoft.app.startup.NormalPostAuthStartupSeam
import com.chaikasoft.app.startup.PostAuthStartupMode
import com.chaikasoft.app.startup.PostAuthStartupSeam
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Named
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [PostAuthStartupModule::class],
)
object E2EPostAuthStartupModule {

    @Provides
    @Singleton
    @Named("postAuthStartupMode")
    fun providePostAuthStartupMode(): PostAuthStartupMode = E2ETestArguments.postAuthStartupMode()

    @Provides
    @Singleton
    fun providePostAuthStartupSeam(
        @Named("postAuthStartupMode") mode: PostAuthStartupMode,
        normal: NormalPostAuthStartupSeam,
        disabled: DisabledPostAuthStartupSeam,
        fakeSuccess: FakeSuccessPostAuthStartupSeam,
    ): PostAuthStartupSeam {
        return when (mode) {
            PostAuthStartupMode.NORMAL -> normal
            PostAuthStartupMode.DISABLED -> disabled
            PostAuthStartupMode.FAKE_SUCCESS -> fakeSuccess
        }
    }
}
