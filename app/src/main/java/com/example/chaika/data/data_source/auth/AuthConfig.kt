package com.example.chaika.data.data_source.auth


/**
 * Конфигурация OAuth 2.0 для авторизации с ZITADEL.
 */
object AuthConfig {
    const val CLIENT_ID = "303649605919244516" // Получите из ZITADEL
    const val REDIRECT_URI = "com.example.chaika:/oauth2redirect" // Должен совпадать с ZITADEL
    const val AUTH_ENDPOINT = "https://<your-zitadel-domain>/oauth/v2/authorize"
    const val TOKEN_ENDPOINT = "https://<your-zitadel-domain>/oauth/v2/token"
    const val SCOPES = "openid profile email offline_access" // Минимальный набор для авторизации
}
