package com.example.chaika.auth

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import com.example.chaika.util.PKCEUtil
import com.example.chaika.auth.AuthConfig
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.AuthState
import net.openid.appauth.ResponseTypeValues
import javax.inject.Inject

class OAuthManager @Inject constructor(
    private val authService: AuthorizationService
) {

    var authState: AuthState? = null
    var codeVerifier: String? = null

    /**
     * Создает Intent для запуска авторизации с PKCE.
     */
    fun createAuthIntent(): Intent {
        Log.d("OAuthManager", "Starting authentication")

        // Задаем эндпоинты и конфигурацию
        val serviceConfig = AuthorizationServiceConfiguration(
            Uri.parse(AuthConfig.AUTH_ENDPOINT),
            Uri.parse(AuthConfig.TOKEN_ENDPOINT)
        )
        val clientId = AuthConfig.CLIENT_ID
        val redirectUri = Uri.parse(AuthConfig.REDIRECT_URI)
        val scope = AuthConfig.SCOPES

        // Генерируем PKCE-параметры
        codeVerifier = PKCEUtil.generateCodeVerifier()
        val codeChallenge = PKCEUtil.generateCodeChallenge(codeVerifier!!)
        Log.d("OAuthManager", "codeVerifier: $codeVerifier")
        Log.d("OAuthManager", "codeChallenge: $codeChallenge")

        // Формируем запрос авторизации с PKCE
        val authRequest = AuthorizationRequest.Builder(
            serviceConfig,
            clientId,
            ResponseTypeValues.CODE,
            redirectUri
        )
            .setScope(scope)
            .setCodeVerifier(codeVerifier, codeChallenge, "S256")
            .build()

        val customTabsIntent = CustomTabsIntent.Builder().build()
        // Получаем Intent для авторизации через AppAuth
        return authService.getAuthorizationRequestIntent(authRequest, customTabsIntent)
    }

    /**
     * Обрабатывает ответ авторизации и выполняет обмен кода на токен.
     *
     * @param data Intent с результатом авторизации.
     * @param onTokenReceived Callback, передающий полученный access token.
     */
    fun handleAuthorizationResponse(data: Intent, onTokenReceived: (String) -> Unit) {
        val response = AuthorizationResponse.fromIntent(data)
        val ex = net.openid.appauth.AuthorizationException.fromIntent(data)
        if (response != null) {
            Log.d("OAuthManager", "AuthorizationResponse: $response")
            authState = AuthState(response, ex)
            val tokenRequest = response.createTokenExchangeRequest()
            Log.d("OAuthManager", "TokenExchangeRequest: $tokenRequest")
            authService.performTokenRequest(tokenRequest) { tokenResponse, exception ->
                if (tokenResponse != null) {
                    authState?.update(tokenResponse, exception)
                    val accessToken = tokenResponse.accessToken
                    if (accessToken != null) {
                        Log.d("OAuthManager", "Access token received: $accessToken")
                        onTokenReceived(accessToken)
                    } else {
                        Log.e("OAuthManager", "Access token is missing")
                    }
                } else {
                    Log.e("OAuthManager", "Token exchange error: ${exception?.errorDescription}")
                }
            }
        } else {
            Log.e(
                "OAuthManager",
                "Authorization error: ${ex?.errorDescription ?: "Response is null"}"
            )
        }
    }
}
