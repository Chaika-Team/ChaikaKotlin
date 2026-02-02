package com.chaikasoft.app.di

import com.chaikasoft.app.BuildConfig
import com.chaikasoft.app.data.dataSource.apiService.ChaikaSoftApiService
import com.chaikasoft.app.data.dataSource.repo.ChaikaTripperRepositoryInterface
import com.chaikasoft.app.data.dataSource.repo.ChaikaSoftApiServiceRepository
import com.chaikasoft.app.data.dataSource.repo.ChaikaSoftApiServiceRepositoryInterface
import com.chaikasoft.app.data.dataSource.repo.ChaikaSoftReportsRepository
import com.chaikasoft.app.data.dataSource.repo.ChaikaSoftReportsRepositoryInterface
import com.chaikasoft.app.data.dataSource.repo.ChaikaTripperRepository
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

    protected open fun baseUrl(): HttpUrl = BuildConfig.CHAIKA_SOFT_URL.toHttpUrl()

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
    ): ChaikaTripperRepositoryInterface =
        ChaikaTripperRepository(service)

    @Provides
    @Singleton
    fun provideChaikaSoftReportsRepository(
        @Named("ChaikaSoftApiService") service: ChaikaSoftApiService
    ): ChaikaSoftReportsRepositoryInterface = ChaikaSoftReportsRepository(service)
}
