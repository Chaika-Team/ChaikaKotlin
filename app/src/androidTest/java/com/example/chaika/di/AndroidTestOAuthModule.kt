package com.example.chaika.di

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.chaika.auth.AuthIntentProviderInterface
import com.example.chaika.auth.OAuthConfig
import com.example.chaika.auth.OAuthManager
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import javax.inject.Singleton

@Suppress("UNCHECKED_CAST")
fun <T> anyNonNull(): T = ArgumentMatchers.any<T>() as T

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [OAuthModule::class],
)
object AndroidTestOAuthModule {
    @Provides
    @Singleton
    fun provideTestOAuthConfig(): OAuthConfig =
        OAuthConfig(
            clientId = "test_client_id",
            redirectUri = "com.example.chaika://testredirect",
            authEndpoint = "https://mocked.auth/authorize",
            tokenEndpoint = "https://mocked.auth/token",
            scopes = "openid profile email",
        )

    @Provides
    @Singleton
    fun provideMockAuthIntentProvider(): AuthIntentProviderInterface {
        val mock = Mockito.mock(AuthIntentProviderInterface::class.java)
        val fakeIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://mocked.intent"))
        // Используем нашу функцию anyNonNull() для безопасного matchera.
        Mockito
            .`when`(mock.getAuthIntent(anyNonNull<AuthorizationRequest>()))
            .thenReturn(fakeIntent)
        return mock
    }

    @Provides
    @Singleton
    fun provideAuthorizationService(
        @ApplicationContext context: Context,
    ): AuthorizationService = AuthorizationService(context)

    @Provides
    @Singleton
    fun provideOAuthManager(
        authService: AuthorizationService,
        authIntentProvider: AuthIntentProviderInterface,
        oAuthConfig: OAuthConfig,
    ): OAuthManager = OAuthManager(authService, authIntentProvider, oAuthConfig)
}
