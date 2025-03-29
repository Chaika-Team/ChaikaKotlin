package com.example.chaika.auth

/**
 * Конфигурация для OAuth 2.0 авторизации.
 * Значения по умолчанию можно брать из статического объекта AuthConfig.
 */
data class OAuthConfig(
    val clientId: String,
    val redirectUri: String,
    val authEndpoint: String,
    val tokenEndpoint: String,
    val scopes: String,
)
