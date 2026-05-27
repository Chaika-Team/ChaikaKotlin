package com.chaikasoft.app.e2e.di

import com.chaikasoft.app.data.local.LocalImageRepositoryInterface
import com.chaikasoft.app.di.FileRepositoriesModule
import com.chaikasoft.app.e2e.fakes.NoOpLocalImageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [FileRepositoriesModule::class],
)
object E2EFileRepositoriesModule {
    @Provides
    @Singleton
    fun provideLocalImageRepository(
        repository: NoOpLocalImageRepository,
    ): LocalImageRepositoryInterface = repository
}
