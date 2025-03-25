package com.example.chaika.di

import android.content.Context
import com.example.chaika.data.inMemory.InMemoryCartRepository
import com.example.chaika.data.inMemory.InMemoryCartRepositoryInterface
import com.example.chaika.data.local.LocalImageRepository
import com.example.chaika.data.local.LocalImageRepositoryInterface
import com.example.chaika.data.local.LocalTripReportRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FileRepositoriesModule {
    @Provides
    @Singleton
    fun provideInMemoryCartRepository(): InMemoryCartRepositoryInterface = InMemoryCartRepository()

    @Provides
    @Singleton
    fun provideLocalImageRepository(
        @ApplicationContext context: Context,
    ): LocalImageRepositoryInterface = LocalImageRepository(context)

    @Provides
    @Singleton
    fun provideLocalTripReportRepository(
        @ApplicationContext context: Context,
    ): LocalTripReportRepository = LocalTripReportRepository(context)
}
