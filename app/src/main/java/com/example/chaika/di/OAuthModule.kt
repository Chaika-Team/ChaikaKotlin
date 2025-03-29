package com.example.chaika.di

import android.content.Context
import com.example.chaika.auth.AuthIntentProvider
import com.example.chaika.auth.AuthIntentProviderInterface
import com.example.chaika.auth.OAuthManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.openid.appauth.AuthorizationService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
open class OAuthModule {
    @Provides
    @Singleton
    fun provideAuthorizationService(
        @ApplicationContext context: Context,
    ): AuthorizationService = AuthorizationService(context)

    @Provides
    @Singleton
    fun provideAuthIntentProvider(authService: AuthorizationService): AuthIntentProviderInterface = AuthIntentProvider(authService)

    @Provides
    @Singleton
    fun provideOAuthManager(
        authService: AuthorizationService,
        authIntentProvider: AuthIntentProviderInterface,
    ): OAuthManager = OAuthManager(authService, authIntentProvider)
}
