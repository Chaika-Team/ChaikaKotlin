package com.example.chaika.di

import android.content.Context
import com.example.chaika.data.inMemory.CartRepositoryFactory
import com.example.chaika.data.inMemory.CartRepositoryFactoryInterface
import com.example.chaika.data.local.LocalImageRepository
import com.example.chaika.data.local.LocalImageRepositoryInterface
import com.example.chaika.data.local.LocalTripReportRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FileRepositoriesModule {

    @Binds
    @Singleton
    abstract fun bindCartRepositoryFactory(
        impl: CartRepositoryFactory
    ): CartRepositoryFactoryInterface

    @Provides
    @Singleton
    fun provideLocalImageRepository(
        @ApplicationContext context: Context
    ): LocalImageRepositoryInterface =
        LocalImageRepository(context)

    @Provides
    @Singleton
    fun provideLocalTripReportRepository(
        @ApplicationContext context: Context
    ): LocalTripReportRepository =
        LocalTripReportRepository(context)
}
