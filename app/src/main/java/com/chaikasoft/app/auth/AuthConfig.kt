package com.chaikasoft.app.auth

import com.chaikasoft.app.BuildConfig

/**
 * Конфигурация OAuth 2.0 для авторизации с ZITADEL.
 */
object AuthConfig {
    val CLIENT_ID: String get() = BuildConfig.CLIENT_ID
    val AUTH_ENDPOINT: String get() = BuildConfig.ZITADEL_URL + "/oauth/v2/authorize"
    val TOKEN_ENDPOINT: String get() = BuildConfig.ZITADEL_URL + "/oauth/v2/token"
    const val REDIRECT_URI = "com.chaikasoft.app://oauth2redirect"
    const val SCOPES = "openid profile email"
}
