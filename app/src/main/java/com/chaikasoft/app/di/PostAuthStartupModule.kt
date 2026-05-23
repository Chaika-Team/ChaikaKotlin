package com.chaikasoft.app.di

import com.chaikasoft.app.startup.DisabledPostAuthStartupSeam
import com.chaikasoft.app.startup.FakeSuccessPostAuthStartupSeam
import com.chaikasoft.app.startup.NormalPostAuthStartupSeam
import com.chaikasoft.app.startup.PostAuthStartupMode
import com.chaikasoft.app.startup.PostAuthStartupSeam
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PostAuthStartupModule {

    @Provides
    @Singleton
    @Named("postAuthStartupMode")
    fun providePostAuthStartupMode(): PostAuthStartupMode = PostAuthStartupMode.NORMAL

    @Provides
    @Singleton
    fun providePostAuthStartupSeam(
        @Named("postAuthStartupMode") mode: PostAuthStartupMode,
        normal: NormalPostAuthStartupSeam,
        disabled: DisabledPostAuthStartupSeam,
        fakeSuccess: FakeSuccessPostAuthStartupSeam
    ): PostAuthStartupSeam = when (mode) {
        PostAuthStartupMode.NORMAL -> normal
        PostAuthStartupMode.DISABLED -> disabled
        PostAuthStartupMode.FAKE_SUCCESS -> fakeSuccess
    }
}
