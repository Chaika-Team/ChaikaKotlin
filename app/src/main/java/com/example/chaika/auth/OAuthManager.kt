package com.example.chaika.auth

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import com.example.chaika.util.PKCEUtil
import net.openid.appauth.AuthorizationException
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

    private var authState: AuthState? = null
    private var codeVerifier: String? = null

    // Сохраняем последний сформированный запрос
    private var lastAuthRequest: AuthorizationRequest? = null

    /**
     * Создает Intent для запуска авторизации с PKCE.
     */
    fun createAuthIntent(): Intent {
        Log.d("OAuthManager", "Starting authentication")

        // Настраиваем конфигурацию эндпоинтов
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

        // Формируем запрос авторизации
        val authRequest = AuthorizationRequest.Builder(
            serviceConfig,
            clientId,
            ResponseTypeValues.CODE,
            redirectUri
        )
            .setScope(scope)
            .setCodeVerifier(codeVerifier, codeChallenge, "S256")
            .build()

        // Сохраняем запрос для последующего построения ответа вручную
        lastAuthRequest = authRequest

        val customTabsIntent = CustomTabsIntent.Builder().build()
        return authService.getAuthorizationRequestIntent(authRequest, customTabsIntent)
    }

    /**
     * Обрабатывает ответ авторизации и выполняет обмен кода на токен.
     *
     * Если Intent не содержит extras (что бывает при deep link'ах в Single-Activity),
     * пытается вручную создать AuthorizationResponse, используя сохранённый AuthorizationRequest.
     *
     * @param data Intent с результатом авторизации.
     * @param onTokenReceived Callback, передающий полученный access token.
     */
    fun handleAuthorizationResponse(data: Intent, onTokenReceived: (String) -> Unit) {
        // Пытаемся получить ответ стандартным способом
        var response = AuthorizationResponse.fromIntent(data)
        var ex = AuthorizationException.fromIntent(data)

        // Если extras отсутствуют, попробуем извлечь данные из URI и создать ответ вручную
        if (response == null && data.data != null && lastAuthRequest != null) {
            Log.d("OAuthManager", "No extras in Intent – parsing URI manually: ${data.data}")
            val uri = data.data!!
            val code = uri.getQueryParameter("code")
            val state = uri.getQueryParameter("state")
            if (code != null && state != null) {
                // Создаем AuthorizationResponse вручную, используя сохранённый запрос
                response = AuthorizationResponse.Builder(lastAuthRequest!!)
                    .setAuthorizationCode(code)
                    .setState(state)
                    .setScope(lastAuthRequest!!.scope)
                    .build()
                ex = null
            }
        }

        Log.d("OAuthManager", "handleAuthorizationResponse: response=$response, exception=$ex")

        if (response != null) {
            authState = AuthState(response, ex)
            val tokenRequest = response.createTokenExchangeRequest()
            Log.d(
                "OAuthManager",
                "TokenExchangeRequest: $tokenRequest, codeVerifier: $codeVerifier"
            )
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
