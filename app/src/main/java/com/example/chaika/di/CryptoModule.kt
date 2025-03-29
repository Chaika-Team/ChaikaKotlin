package com.example.chaika.di

import android.content.Context
import com.example.chaika.data.crypto.EncryptedTokenManager
import com.example.chaika.data.crypto.EncryptedTokenManagerInterface
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
    ): EncryptedTokenManagerInterface = EncryptedTokenManager(context)
}
