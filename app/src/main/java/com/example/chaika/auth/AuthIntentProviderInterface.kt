package com.example.chaika.auth

import android.content.Intent
import net.openid.appauth.AuthorizationRequest

/**
 * Интерфейс, который предоставляет Intent для запуска авторизации.
 * Реализация отвечает только за UI-часть (например, открытие CustomTabs).
 */
interface AuthIntentProviderInterface {
    fun getAuthIntent(authRequest: AuthorizationRequest): Intent
}
