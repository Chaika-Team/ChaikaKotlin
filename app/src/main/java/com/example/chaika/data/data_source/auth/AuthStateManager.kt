package com.example.chaika.data.data_source.auth

import android.app.Application
import android.util.Log
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.TokenResponse

/**
 * Менеджер состояния авторизации.
 */
class AuthStateManager private constructor(
    private val application: Application
) {
    private val prefs = application.getSharedPreferences("auth", Application.MODE_PRIVATE)

    /**
     * Сохраняем AuthState в SharedPreferences.
     *
     * @param state Состояние авторизации, которое нужно сохранить.
     */
    fun saveState(state: AuthState) {
        prefs.edit().putString("authState", state.jsonSerializeString()).apply()
    }

    /**
     * Загружаем AuthState из SharedPreferences.
     *
     * @return Загруженное состояние авторизации или новое состояние, если ничего не найдено.
     */
    fun readState(): AuthState {
        val stateJson = prefs.getString("authState", null)
        return try {
            if (stateJson != null) {
                AuthState.jsonDeserialize(stateJson)
            } else {
                AuthState()
            }
        } catch (e: Exception) {
            Log.e("AuthStateManager", "Failed to deserialize AuthState", e)
            AuthState()
        }
    }

    /**
     * Обновляет AuthState после ответа на запрос авторизации.
     *
     * @param response Ответ на запрос авторизации.
     */
    fun updateAfterAuthorizationResponse(response: AuthorizationResponse?) {
        val authState = readState()
        authState.update(response, null)
        saveState(authState)
    }

    /**
     * Обновляет AuthState после ответа на запрос токена.
     *
     * @param tokenResponse Ответ на запрос токена.
     */
    fun updateAfterTokenResponse(tokenResponse: TokenResponse?) {
        val authState = readState()
        authState.update(tokenResponse, null)
        saveState(authState)
    }

    /**
     * Сбрасывает состояние авторизации (например, при выходе пользователя).
     */
    fun clearState() {
        prefs.edit().remove("authState").apply()
    }

    companion object {
        @Volatile
        private var instance: AuthStateManager? = null

        /**
         * Получает или создаёт инстанс `AuthStateManager`.
         *
         * @param application Экземпляр Application для контекста.
         * @return Экземпляр `AuthStateManager`.
         */
        fun getInstance(application: Application): AuthStateManager {
            return instance ?: synchronized(this) {
                instance ?: AuthStateManager(application).also { instance = it }
            }
        }
    }
}
