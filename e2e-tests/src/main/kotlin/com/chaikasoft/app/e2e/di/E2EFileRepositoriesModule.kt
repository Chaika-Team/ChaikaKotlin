package com.chaikasoft.app.e2e.di

import android.content.Context
import com.chaikasoft.app.data.local.LocalImageRepositoryInterface
import com.chaikasoft.app.data.local.LocalTripReportRepository
import com.chaikasoft.app.di.FileRepositoriesModule
import com.chaikasoft.app.e2e.fakes.NoOpLocalImageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [FileRepositoriesModule::class],
)
object E2EFileRepositoriesModule {
    @Provides
    @Singleton
    fun provideLocalImageRepository(
        repository: NoOpLocalImageRepository,
    ): LocalImageRepositoryInterface = repository

    @Provides
    @Singleton
    fun provideLocalTripReportRepository(
        @ApplicationContext context: Context,
    ): LocalTripReportRepository = LocalTripReportRepository(context)
}
