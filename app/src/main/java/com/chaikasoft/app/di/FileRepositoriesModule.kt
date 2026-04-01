package com.chaikasoft.app.di

import android.content.Context
import com.chaikasoft.app.data.local.LocalImageRepository
import com.chaikasoft.app.data.local.LocalImageRepositoryInterface
import com.chaikasoft.app.data.local.LocalTripReportRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FileRepositoriesModule {

    @Provides
    @Singleton
    fun provideLocalImageRepository(
        @ApplicationContext context: Context,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): LocalImageRepositoryInterface =
        LocalImageRepository(context = context, ioDispatcher = ioDispatcher)

    @Provides
    @Singleton
    fun provideLocalTripReportRepository(
        @ApplicationContext context: Context
    ): LocalTripReportRepository =
        LocalTripReportRepository(context)
}
