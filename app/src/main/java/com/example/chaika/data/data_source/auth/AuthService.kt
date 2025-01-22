package com.example.chaika.data.data_source.auth

import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import net.openid.appauth.*
import javax.inject.Inject

/**
 * Сервис авторизации через AppAuth.
 */
class AuthService @Inject constructor(
    private val application: Application
) {

    private val serviceConfig = AuthorizationServiceConfiguration(
        Uri.parse(AuthConfig.AUTH_ENDPOINT),
        Uri.parse(AuthConfig.TOKEN_ENDPOINT)
    )

    private val authRequest = AuthorizationRequest.Builder(
        serviceConfig,
        AuthConfig.CLIENT_ID,
        ResponseTypeValues.CODE,
        Uri.parse(AuthConfig.REDIRECT_URI)
    )
        .setScopes(*AuthConfig.SCOPES.split(" ").toTypedArray())
        .build()

    private val authService = AuthorizationService(application)

    fun startAuthorization() {
        val customTabsIntent =
            authService.createCustomTabsIntentBuilder(authRequest.toUri()).build()

        val pendingIntent = PendingIntent.getActivity(
            application,
            0,
            Intent(),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        authService.performAuthorizationRequest(
            authRequest,
            pendingIntent,
            customTabsIntent
        )
    }

    fun handleAuthorizationResponse(intent: Intent, callback: (AuthState?, Throwable?) -> Unit) {
        val response = AuthorizationResponse.fromIntent(intent)
        val exception = AuthorizationException.fromIntent(intent)

        if (response != null) {
            val authState = AuthState()
            authState.update(
                response,
                null
            )

            authService.performTokenRequest(response.createTokenExchangeRequest()) { tokenResponse, ex ->
                if (tokenResponse != null) {
                    authState.update(tokenResponse, null)
                    AuthStateManager.getInstance(application)
                        .updateAfterTokenResponse(tokenResponse)
                    callback(authState, null)
                } else {
                    callback(null, ex)
                }
            }
        } else {
            callback(null, exception)
        }
    }
}
