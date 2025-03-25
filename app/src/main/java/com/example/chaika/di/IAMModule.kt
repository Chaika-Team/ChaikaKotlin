package com.example.chaika.di

import com.example.chaika.data.dataSource.apiService.IAMApiService
import com.example.chaika.data.dataSource.repo.IAMApiServiceRepository
import com.example.chaika.data.dataSource.repo.IAMApiServiceRepositoryInterface
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
object IAMModule {
    @Provides
    @Singleton
    @Named("IAMRetrofit")
    fun provideIAMRetrofit(): Retrofit =
        Retrofit
            .Builder()
            .baseUrl("https://iam.remystorage.ru/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideIAMApiService(
        @Named("IAMRetrofit") retrofit: Retrofit,
    ): IAMApiService = retrofit.create(IAMApiService::class.java)

    @Provides
    @Singleton
    fun provideIAMApiServiceRepository(service: IAMApiService): IAMApiServiceRepositoryInterface = IAMApiServiceRepository(service)
}
