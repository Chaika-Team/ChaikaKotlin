package com.chaikasoft.app.e2e.di

import com.chaikasoft.app.BuildConfig
import com.chaikasoft.app.data.datasource.apiservice.IAMApiService
import com.chaikasoft.app.data.datasource.repo.IAMApiServiceRepository
import com.chaikasoft.app.data.datasource.repo.IAMApiServiceRepositoryInterface
import com.chaikasoft.app.di.IAMModule
import com.chaikasoft.app.e2e.config.E2ETestArguments
import com.chaikasoft.app.e2e.fakes.FakeIAMApiServiceRepository
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
    replaces = [IAMModule::class],
)
object E2EIAMModule {
    private fun createIamApiService(): IAMApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.ZITADEL_URL.toHttpUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(IAMApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideIamRepository(
        repository: FakeIAMApiServiceRepository,
    ): IAMApiServiceRepositoryInterface {
        return if (E2ETestArguments.isEnvMode()) {
            IAMApiServiceRepository(createIamApiService())
        } else {
            repository
        }
    }
}
