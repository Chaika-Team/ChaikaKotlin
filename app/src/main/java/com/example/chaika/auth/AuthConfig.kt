package com.example.chaika.auth

import com.example.chaika.BuildConfig

/**
 * Конфигурация OAuth 2.0 для авторизации с ZITADEL.
 */
object AuthConfig {
    val CLIENT_ID: String get() = BuildConfig.CLIENT_ID
    val AUTH_ENDPOINT: String get() = BuildConfig.ZITADEL_URL + "/oauth/v2/authorize"
    val TOKEN_ENDPOINT: String get() = BuildConfig.ZITADEL_URL + "/oauth/v2/token"
    const val REDIRECT_URI = "com.example.chaika://oauth2redirect" // TODO: Refactoring
    const val SCOPES = "openid profile email"
}
