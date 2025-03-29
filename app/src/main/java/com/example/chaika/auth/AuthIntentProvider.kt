package com.example.chaika.auth

import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService

/**
 * Реальная реализация AuthIntentProvider, использующая CustomTabs.
 */
class AuthIntentProvider(
    private val authService: AuthorizationService,
) : AuthIntentProviderInterface {
    override fun getAuthIntent(authRequest: AuthorizationRequest): Intent {
        val customTabsIntent = CustomTabsIntent.Builder().build()
        return authService.getAuthorizationRequestIntent(authRequest, customTabsIntent)
    }
}
