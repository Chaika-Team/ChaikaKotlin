package com.chaikasoft.app.di

import com.chaikasoft.app.data.inmemory.CartRepositoryFactory
import com.chaikasoft.app.data.inmemory.CartRepositoryFactoryInterface
import com.chaikasoft.app.data.settings.AppCompatLanguageRepository
import com.chaikasoft.app.data.settings.LanguageRepositoryInterface
import com.chaikasoft.app.data.settings.SettingsRepository
import com.chaikasoft.app.data.settings.SettingsRepositoryInterface
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FactoryModule {

    @Binds
    @Singleton
    abstract fun bindCartRepositoryFactory(
        impl: CartRepositoryFactory
    ): CartRepositoryFactoryInterface

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepository): SettingsRepositoryInterface

    @Binds
    @Singleton
    abstract fun bindLanguageRepository(
        impl: AppCompatLanguageRepository
    ): LanguageRepositoryInterface
}
