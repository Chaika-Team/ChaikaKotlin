package com.example.chaika.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.TokenRequest
import net.openid.appauth.TokenResponse
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Dummy-реализация AuthorizationService для тестирования OAuthManager.
 *
 * Переопределяет необходимые методы так, чтобы не выполнялся реальный HTTP-запрос и
 * возвращались заранее заданные значения.
 */
open class DummyAuthorizationService(
    context: Context
) : AuthorizationService(context) {

    override fun getAuthorizationRequestIntent(
        request: AuthorizationRequest,
        customTabsIntent: CustomTabsIntent
    ): Intent {
        // Возвращаем фиктивный Intent с нужным действием.
        return Intent("dummy_action")
    }

    // Базовая реализация performTokenRequest не используется,
    // её переопределяют в тестах.
}

/**
 * Тесты для OAuthManager.
 *
 * Проверяются:
 * - Корректное создание Intent для авторизации через createAuthIntent().
 * - Правильная обработка deep link'а в handleAuthorizationResponse() с вызовом performTokenRequest,
 *   возвращающего access token.
 */
@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [28])
class OAuthManagerTest {

    @Test
    fun createAuthIntent_returnsExpectedIntent() {
        // Arrange:
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dummyAuthService = DummyAuthorizationService(context)
        val oauthManager = OAuthManager(dummyAuthService)

        // Act:
        val resultIntent = oauthManager.createAuthIntent()

        // Assert:
        assertEquals("dummy_action", resultIntent.action)
    }

    @Test
    fun handleAuthorizationResponse_processesDeepLink_andInvokesCallbackWithAccessToken() {
        // Arrange:
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Переопределяем DummyAuthorizationService так, чтобы при вызове performTokenRequest
        // создавался реальный TokenResponse с accessToken = "dummy_token"
        val dummyAuthService = object : DummyAuthorizationService(context) {
            override fun getAuthorizationRequestIntent(
                request: AuthorizationRequest,
                customTabsIntent: CustomTabsIntent
            ): Intent {
                return Intent("dummy_action")
            }

            override fun performTokenRequest(
                request: TokenRequest,
                callback: TokenResponseCallback
            ) {
                // Создаём TokenResponse через Builder, используя переданный request
                val tokenResponse = TokenResponse.Builder(request)
                    .setAccessToken("dummy_token")
                    .build()
                callback.onTokenRequestCompleted(tokenResponse, null)
            }
        }
        val oauthManager = OAuthManager(dummyAuthService)

        // Вызываем createAuthIntent() для установки внутреннего состояния (lastAuthRequest)
        oauthManager.createAuthIntent()

        // Создаем Intent, имитирующий deep link с URI, содержащим параметры code и state
        val deepLinkUri = Uri.parse("${AuthConfig.REDIRECT_URI}?code=dummy_code&state=dummy_state")
        val deepLinkIntent = Intent().apply { data = deepLinkUri }

        // Act:
        var capturedToken: String? = null
        oauthManager.handleAuthorizationResponse(deepLinkIntent) { token ->
            capturedToken = token
        }

        // Assert:
        assertEquals("dummy_token", capturedToken)
    }
}
