package com.example.chaika.auth

import android.content.Intent
import android.net.Uri
import android.util.Log
import com.example.chaika.util.PKCEUtil
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import javax.inject.Inject

/**
 * OAuthManager управляет процессом авторизации с PKCE через AppAuth.
 */
open class OAuthManager
    @Inject
    constructor(
        private val authService: AuthorizationService,
        private val authIntentProvider: AuthIntentProviderInterface,
        private val oauthConfig: OAuthConfig,
    ) {
        private var authState: AuthState? = null
        private var codeVerifier: String? = null

        // Сохраняем последний сформированный запрос для ручного парсинга URI
        private var lastAuthRequest: AuthorizationRequest? = null

        /**
         * Создает Intent для запуска авторизации с PKCE.
         */
        open fun createAuthIntent(): Intent {
            Log.d("OAuthManager", "Starting authentication")
            // Настраиваем конфигурацию эндпоинтов из oauthConfig
            val serviceConfig =
                AuthorizationServiceConfiguration(
                    Uri.parse(oauthConfig.authEndpoint),
                    Uri.parse(oauthConfig.tokenEndpoint),
                )
            val clientId = oauthConfig.clientId
            val redirectUri = Uri.parse(oauthConfig.redirectUri)
            val scope = oauthConfig.scopes

            // Генерируем PKCE-параметры
            codeVerifier = PKCEUtil.generateCodeVerifier()
            val codeChallenge = PKCEUtil.generateCodeChallenge(codeVerifier!!)
            Log.d("OAuthManager", "codeVerifier: $codeVerifier")
            Log.d("OAuthManager", "codeChallenge: $codeChallenge")

            // Формируем запрос авторизации
            val authRequest =
                AuthorizationRequest
                    .Builder(
                        serviceConfig,
                        clientId,
                        ResponseTypeValues.CODE,
                        redirectUri,
                    ).setScope(scope)
                    .setCodeVerifier(codeVerifier, codeChallenge, "S256")
                    .build()

            // Сохраняем запрос для последующего ручного парсинга
            lastAuthRequest = authRequest

            // Получаем Intent через AuthIntentProvider, который отвечает за UI (CustomTabs)
            return authIntentProvider.getAuthIntent(authRequest)
        }

        /**
         * Обрабатывает ответ авторизации и выполняет обмен кода на токен.
         *
         * Если стандартный способ получения ответа не сработал (extras отсутствуют),
         * пытается вручную создать AuthorizationResponse, используя сохранённый AuthorizationRequest.
         *
         * @param data Intent с результатом авторизации.
         * @param onTokenReceived Callback, передающий полученный access token.
         */
        fun handleAuthorizationResponse(
            data: Intent,
            onTokenReceived: (String) -> Unit,
        ) {
            // Пытаемся получить ответ стандартным способом
            var response = AuthorizationResponse.fromIntent(data)
            var ex = AuthorizationException.fromIntent(data)

            // Если extras отсутствуют, пытаемся создать ответ вручную
            if (response == null && data.data != null && lastAuthRequest != null) {
                val (manualResponse, manualError) = parseResponseManually(data)
                response = manualResponse
                ex = manualError as? AuthorizationException
            }

            Log.d("OAuthManager", "handleAuthorizationResponse: response=$response, exception=$ex")

            if (response != null) {
                authState = AuthState(response, ex)
                val tokenRequest = response.createTokenExchangeRequest()
                Log.d(
                    "OAuthManager",
                    "TokenExchangeRequest: $tokenRequest, codeVerifier: $codeVerifier",
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
                    "Authorization error: ${ex?.errorDescription ?: "Response is null"}",
                )
            }
        }

        /**
         * Вспомогательный метод для ручного парсинга AuthorizationResponse из URI.
         *
         * @param data Intent с данными авторизации.
         * @return Пара: (AuthorizationResponse?, Throwable?) – если парсинг успешен, Throwable равен null.
         */
        private fun parseResponseManually(data: Intent): Pair<AuthorizationResponse?, Throwable?> {
            Log.d("OAuthManager", "No extras in Intent – parsing URI manually: ${data.data}")
            val uri = data.data!!
            val code = uri.getQueryParameter("code")
            val state = uri.getQueryParameter("state")
            return if (code != null && state != null) {
                val manualResponse =
                    AuthorizationResponse
                        .Builder(lastAuthRequest!!)
                        .setAuthorizationCode(code)
                        .setState(state)
                        .setScope(lastAuthRequest!!.scope)
                        .build()
                manualResponse to null
            } else {
                null to Exception("Manual parsing failed")
            }
        }
    }
