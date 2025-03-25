package com.example.chaika.di

import com.example.chaika.data.dataSource.apiService.ChaikaSoftApiService
import com.example.chaika.data.dataSource.repo.ChaikaSoftApiServiceRepository
import com.example.chaika.data.dataSource.repo.ChaikaSoftApiServiceRepositoryInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Provides
    @Singleton
    @Named("ChaikaSoftRetrofit")
    fun provideChaikaSoftRetrofit(): Retrofit =
        Retrofit
            .Builder()
            .baseUrl("https://chaika-soft.ru/")
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
}
