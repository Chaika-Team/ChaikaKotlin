package com.chaikasoft.app.e2e.di

import com.chaikasoft.app.data.crypto.EncryptedTokenManagerInterface
import com.chaikasoft.app.di.CryptoModule
import com.chaikasoft.app.e2e.fakes.InMemoryTokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [CryptoModule::class],
)
object E2ECryptoModule {
    @Provides
    @Singleton
    fun provideTokenManager(
        tokenManager: InMemoryTokenManager,
    ): EncryptedTokenManagerInterface = tokenManager
}
