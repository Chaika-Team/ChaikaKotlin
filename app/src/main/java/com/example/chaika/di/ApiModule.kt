package com.example.chaika.di

import com.example.chaika.data.dataSource.apiService.ChaikaSoftApiService
import com.example.chaika.data.dataSource.repo.ChaikaRoutesAdapterApiServiceRepositoryInterface
import com.example.chaika.data.dataSource.repo.ChaikaSoftApiServiceRepository
import com.example.chaika.data.dataSource.repo.ChaikaSoftApiServiceRepositoryInterface
import com.example.chaika.data.dataSource.repo.ChaikaSoftRoutesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import okhttp3.HttpUrl
import dagger.hilt.components.SingletonComponent
import okhttp3.HttpUrl.Companion.toHttpUrl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
open class ApiModule {

    protected open fun baseUrl(): HttpUrl = "https://chaika-soft.ru/".toHttpUrl()

    @Provides
    @Singleton
    @Named("ChaikaSoftRetrofit")
    fun provideChaikaSoftRetrofit(): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(baseUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    @Named("ChaikaSoftApiService")
    fun provideChaikaSoftApiService(
        @Named("ChaikaSoftRetrofit") retrofit: Retrofit,
    ): ChaikaSoftApiService = retrofit.create(ChaikaSoftApiService::class.java)

    @Provides
    @Singleton
    fun provideChaikaSoftApiServiceRepository(
        @Named("ChaikaSoftApiService") service: ChaikaSoftApiService,
    ): ChaikaSoftApiServiceRepositoryInterface = ChaikaSoftApiServiceRepository(service)

    @Provides
    @Singleton
    fun provideChaikaRoutesAdapterRepository(
        @Named("ChaikaSoftApiService") service: ChaikaSoftApiService
    ): ChaikaRoutesAdapterApiServiceRepositoryInterface =
        ChaikaSoftRoutesRepository(service)
}
