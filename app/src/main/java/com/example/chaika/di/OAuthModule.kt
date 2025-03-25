package com.example.chaika.di

import android.content.Context
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
object OAuthModule {
    @Provides
    @Singleton
    fun provideAuthorizationService(
        @ApplicationContext context: Context,
    ): AuthorizationService = AuthorizationService(context)

    @Provides
    @Singleton
    fun provideOAuthManager(authService: AuthorizationService): OAuthManager = OAuthManager(authService)
}
