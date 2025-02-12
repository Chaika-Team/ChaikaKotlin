package com.example.chaika.auth


/**
 * Конфигурация OAuth 2.0 для авторизации с ZITADEL.
 */
object AuthConfig {
    const val CLIENT_ID = "303649605919244516" // Получите из ZITADEL
    const val REDIRECT_URI = "com.example.chaika://oauth2redirect"
    const val AUTH_ENDPOINT = "https://iam.remystorage.ru/oauth/v2/authorize"
    const val TOKEN_ENDPOINT = "https://iam.remystorage.ru/oauth/v2/token"
    const val SCOPES = "openid profile email"
}

