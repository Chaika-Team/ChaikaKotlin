package com.example.chaika.di

import com.example.chaika.data.dataSource.apiService.IAMApiService
import com.example.chaika.data.dataSource.repo.IAMApiServiceRepository
import com.example.chaika.data.dataSource.repo.IAMApiServiceRepositoryInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
open class IAMModule {

    // Функция для получения базового URL. При необходимости её можно переопределить.
    protected open fun baseUrl(): HttpUrl = "https://iam.remystorage.ru/".toHttpUrl()

    @Provides
    @Singleton
    @Named("IAMRetrofit")
    fun provideIAMRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideIAMApiService(
        @Named("IAMRetrofit") retrofit: Retrofit,
    ): IAMApiService = retrofit.create(IAMApiService::class.java)

    @Provides
    @Singleton
    fun provideIAMApiServiceRepository(
        service: IAMApiService
    ): IAMApiServiceRepositoryInterface = IAMApiServiceRepository(service)
}
