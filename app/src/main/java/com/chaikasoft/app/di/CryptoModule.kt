package com.chaikasoft.app.di

import android.content.Context
import com.chaikasoft.app.data.crypto.EncryptedTokenManager
import com.chaikasoft.app.data.crypto.EncryptedTokenManagerInterface
import com.chaikasoft.app.diagnostics.ErrorReporter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
open class CryptoModule {
    @Provides
    @Singleton
    fun provideEncryptedTokenManager(
        @ApplicationContext context: Context,
        errorReporter: ErrorReporter
    ): EncryptedTokenManagerInterface = EncryptedTokenManager(context, errorReporter)
}
