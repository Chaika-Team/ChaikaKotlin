package com.chaikasoft.app.e2e.di

import com.chaikasoft.app.BuildConfig
import com.chaikasoft.app.data.datasource.apiservice.ChaikaSoftApiService
import com.chaikasoft.app.data.datasource.repo.ChaikaSoftApiServiceRepository
import com.chaikasoft.app.data.datasource.repo.ChaikaSoftApiServiceRepositoryInterface
import com.chaikasoft.app.data.datasource.repo.ChaikaSoftReportsRepository
import com.chaikasoft.app.data.datasource.repo.ChaikaSoftReportsRepositoryInterface
import com.chaikasoft.app.data.datasource.repo.ChaikaTripperRepository
import com.chaikasoft.app.data.datasource.repo.ChaikaTripperRepositoryInterface
import com.chaikasoft.app.di.ApiModule
import com.chaikasoft.app.e2e.config.E2ETestArguments
import com.chaikasoft.app.e2e.fakes.FakeChaikaSoftApiServiceRepository
import com.chaikasoft.app.e2e.fakes.FakeChaikaSoftReportsRepository
import com.chaikasoft.app.e2e.fakes.FakeChaikaTripperRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import okhttp3.HttpUrl.Companion.toHttpUrl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [ApiModule::class],
)
object E2EApiModule {
    private fun createChaikaSoftApiService(): ChaikaSoftApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.CHAIKA_SOFT_URL.toHttpUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ChaikaSoftApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideChaikaSoftApiServiceRepository(
        repository: FakeChaikaSoftApiServiceRepository,
    ): ChaikaSoftApiServiceRepositoryInterface {
        return if (E2ETestArguments.isEnvMode()) {
            ChaikaSoftApiServiceRepository(createChaikaSoftApiService())
        } else {
            repository
        }
    }

    @Provides
    @Singleton
    fun provideChaikaTripperRepository(
        repository: FakeChaikaTripperRepository,
    ): ChaikaTripperRepositoryInterface {
        return if (E2ETestArguments.isEnvMode()) {
            ChaikaTripperRepository(createChaikaSoftApiService())
        } else {
            repository
        }
    }

    @Provides
    @Singleton
    fun provideChaikaSoftReportsRepository(
        repository: FakeChaikaSoftReportsRepository,
    ): ChaikaSoftReportsRepositoryInterface {
        return if (E2ETestArguments.isEnvMode()) {
            ChaikaSoftReportsRepository(createChaikaSoftApiService())
        } else {
            repository
        }
    }
}
