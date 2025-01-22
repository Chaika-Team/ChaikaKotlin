package com.example.chaika.domain.usecases

import android.content.Intent
import com.example.chaika.data.data_source.auth.AuthService
import com.example.chaika.data.data_source.auth.AuthStateManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.openid.appauth.AuthState
import javax.inject.Inject

/**
 * Юзкейс для выполнения авторизации пользователя.
 *
 * @param authService Сервис для работы с AppAuth.
 * @param authStateManager Менеджер состояния авторизации.
 */
class PerformAuthorizationUseCase @Inject constructor(
    private val authService: AuthService,
    private val authStateManager: AuthStateManager
) {

    /**
     * Выполняет процесс авторизации.
     *
     * @param intent Intent с данными авторизации.
     * @return Access Token, если авторизация успешна.
     * @throws Exception в случае ошибки авторизации.
     */
    suspend operator fun invoke(intent: Intent): String = withContext(Dispatchers.IO) {
        var accessToken: String? = null

        // Обработка ответа авторизации
        authService.handleAuthorizationResponse(intent) { authState, exception ->
            if (authState != null) {
                accessToken = authState.accessToken
                authStateManager.saveState(authState) // Сохраняем состояние авторизации
            } else {
                throw exception ?: Exception("Не удалось выполнить авторизацию")
            }
        }

        // Проверяем, был ли успешно получен токен
        accessToken ?: throw Exception("Токен доступа не найден")
    }
}

/**
 * Юзкейс для запуска процесса авторизации.
 *
 * @param authService Сервис для работы с AppAuth.
 */
class StartAuthorizationUseCase @Inject constructor(
    private val authService: AuthService
) {

    /**
     * Запускает процесс авторизации через AuthService.
     */
    operator fun invoke() {
        authService.startAuthorization()
    }
}

/**
 * Юзкейс для выполнения выхода из аккаунта.
 *
 * @param authStateManager Менеджер состояния авторизации.
 */
class LogoutUseCase @Inject constructor(
    private val authStateManager: AuthStateManager
) {

    /**
     * Выполняет процесс выхода из аккаунта.
     *
     * @throws Exception в случае ошибки.
     */
    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        // Очищаем состояние авторизации
        authStateManager.clearState()
    }
}
