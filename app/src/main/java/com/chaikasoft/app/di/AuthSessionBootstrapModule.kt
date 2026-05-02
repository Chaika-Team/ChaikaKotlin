package com.chaikasoft.app.di

import com.chaikasoft.app.auth.AuthSessionBootstrap
import com.chaikasoft.app.auth.NoOpAuthSessionBootstrap
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthSessionBootstrapModule {

    @Provides
    @Singleton
    fun provideAuthSessionBootstrap(bootstrap: NoOpAuthSessionBootstrap): AuthSessionBootstrap =
        bootstrap
}
