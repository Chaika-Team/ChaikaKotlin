package com.example.chaika.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import net.openid.appauth.*
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Тесты для OAuthManager.
 *
 * Техники тест-дизайна:
 *   - #1 Классы эквивалентности
 *   - #2 Граничные значения / Прогнозирование ошибок
 *
 * Автор: OwletsFox
 *
 * Описание:
 *   - Метод createAuthIntent() должен возвращать Intent, полученный от authService.getAuthorizationRequestIntent().
 *   - Метод handleAuthorizationResponse() должен корректно обрабатывать deep link-ответ:
 *     извлекать параметры code и state, создавать AuthorizationResponse (если extras отсутствуют) и через вызов
 *     performTokenRequest передавать в callback access token.
 */
@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [28])
class OAuthManagerTest {

    @Test
    fun createAuthIntent_returnsExpectedIntent() {
        // Arrange:
        val context = ApplicationProvider.getApplicationContext<Context>()
        val mockAuthService: AuthorizationService = mock()
        val oauthManager = OAuthManager(mockAuthService)

        val dummyIntent = Intent("dummy_action")
        // Stub: используем doReturn() для метода getAuthorizationRequestIntent()
        doReturn(dummyIntent)
            .`when`(mockAuthService)
            .getAuthorizationRequestIntent(any<AuthorizationRequest>(), any<CustomTabsIntent>())

        // Act:
        val resultIntent = oauthManager.createAuthIntent()

        // Assert:
        assertEquals("dummy_action", resultIntent.action)
    }

//    @Test
//    fun handleAuthorizationResponse_processesDeepLink_andInvokesCallbackWithAccessToken() = runTest {
//        // Arrange:
//        val context = ApplicationProvider.getApplicationContext<Context>()
//        val mockAuthService: AuthorizationService = mock()
//        val oauthManager = OAuthManager(mockAuthService)
//
//        // Stub для getAuthorizationRequestIntent – возвращаем фиктивный Intent.
//        val dummyIntent = Intent("dummy_action")
//        doReturn(dummyIntent)
//            .`when`(mockAuthService)
//            .getAuthorizationRequestIntent(any<AuthorizationRequest>(), any<CustomTabsIntent>())
//
//        // Вызываем createAuthIntent() для установки внутреннего состояния (lastAuthRequest).
//        oauthManager.createAuthIntent()
//
//        // Создаем Intent, имитирующий deep link с URI, содержащим параметры code и state.
//        val deepLinkUri = Uri.parse("${AuthConfig.REDIRECT_URI}?code=dummy_code&state=dummy_state")
//        val deepLinkIntent = Intent().apply { data = deepLinkUri }
//
//        // Мокаем фиктивный TokenResponse, возвращающий accessToken "dummy_token".
//        val dummyTokenResponse: TokenResponse = mock()
//        whenever(dummyTokenResponse.accessToken).thenReturn("dummy_token")
//
//        // Stub для performTokenRequest – вызываем callback с dummyTokenResponse.
//        // Используем doAnswer, чтобы при вызове performTokenRequest сразу вызвать callback.
//        doAnswer { invocation ->
//            val callback = invocation.getArgument<(TokenResponse?, AuthorizationException?) -> Unit>(1)
//            callback(dummyTokenResponse, null)
//            null
//        }.`when`(mockAuthService).performTokenRequest(any<TokenRequest>(), any())
//
//        // Act:
//        var capturedToken: String? = null
//        oauthManager.handleAuthorizationResponse(deepLinkIntent) { token ->
//            capturedToken = token
//        }
//
//        // Assert:
//        assertEquals("dummy_token", capturedToken)
//    }
}
