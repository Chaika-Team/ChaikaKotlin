package com.chaikasoft.app.e2e.di

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.chaikasoft.app.auth.AuthConfig
import com.chaikasoft.app.auth.AuthIntentProvider
import com.chaikasoft.app.auth.AuthIntentProviderInterface
import com.chaikasoft.app.auth.OAuthConfig
import com.chaikasoft.app.auth.OAuthManager
import com.chaikasoft.app.di.OAuthModule
import com.chaikasoft.app.e2e.config.E2ETestArguments
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [OAuthModule::class],
)
object E2EOAuthModule {
    @Provides
    @Singleton
    fun provideTestOAuthConfig(): OAuthConfig =
        if (E2ETestArguments.isEnvMode()) {
            OAuthConfig(
                clientId = AuthConfig.CLIENT_ID,
                redirectUri = AuthConfig.REDIRECT_URI,
                authEndpoint = AuthConfig.AUTH_ENDPOINT,
                tokenEndpoint = AuthConfig.TOKEN_ENDPOINT,
                scopes = AuthConfig.SCOPES,
            )
        } else {
            OAuthConfig(
                clientId = "e2e-client-id",
                redirectUri = "com.chaikasoft.app://e2e",
                authEndpoint = "https://e2e.local/authorize",
                tokenEndpoint = "https://e2e.local/token",
                scopes = "openid profile email",
            )
        }

    @Provides
    @Singleton
    fun provideAuthorizationService(
        @ApplicationContext context: Context,
    ): AuthorizationService = AuthorizationService(context)

    @Provides
    @Singleton
    fun provideAuthIntentProvider(
        authService: AuthorizationService,
    ): AuthIntentProviderInterface {
        return if (E2ETestArguments.isEnvMode()) {
            AuthIntentProvider(authService)
        } else {
            object : AuthIntentProviderInterface {
                override fun getAuthIntent(authRequest: AuthorizationRequest): Intent {
                    return Intent(Intent.ACTION_VIEW, Uri.parse("https://e2e.local/auth"))
                }
            }
        }
    }

    @Provides
    @Singleton
    fun provideOAuthManager(
        authService: AuthorizationService,
        authIntentProvider: AuthIntentProviderInterface,
        oAuthConfig: OAuthConfig,
    ): OAuthManager = OAuthManager(authService, authIntentProvider, oAuthConfig)
}
