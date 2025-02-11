package com.example.chaika.domain.usecases

import android.content.Intent
import com.example.chaika.data.crypto.EncryptedTokenManagerInterface
import com.example.chaika.auth.OAuthManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Use case для запуска авторизации.
 * Возвращает Intent, который необходимо передать в ActivityResultLauncher.
 */
class StartAuthorizationUseCase @Inject constructor(
    private val oAuthManager: OAuthManager
) {
    operator fun invoke(): Intent {
        return oAuthManager.createAuthIntent()
    }
}

/**
 * Use case для обработки ответа авторизации.
 * Принимает Intent, полученный в результате авторизации, и возвращает access token.
 */
class HandleAuthorizationResponseUseCase @Inject constructor(
    private val oAuthManager: OAuthManager
) {
    suspend operator fun invoke(intent: Intent): String = suspendCancellableCoroutine { cont ->
        oAuthManager.handleAuthorizationResponse(intent) { token ->
            if (token.isNotEmpty()) {
                cont.resume(token)
            } else {
                cont.resumeWithException(Exception("Получен пустой токен"))
            }
        }
        // При отмене корутины можно добавить обработку отмены, если необходимо.
    }
}

/**
 * Use case для получения сохранённого access token.
 * Он обращается к менеджеру токенов и возвращает токен, если он сохранён.
 */
class GetAccessTokenUseCase @Inject constructor(
    private val tokenManager: EncryptedTokenManagerInterface
) {
    suspend operator fun invoke(): String? = withContext(Dispatchers.IO) {
        tokenManager.getToken()
    }
}

/**
 * Use case для выхода из аккаунта (logout).
 * Он очищает сохранённый токен через менеджер токенов.
 */
class LogoutUseCase @Inject constructor(
    private val tokenManager: EncryptedTokenManagerInterface
) {
    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        tokenManager.clearToken()
    }
}