package com.chaikasoft.app.ui.viewmodels

import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaikasoft.app.auth.AuthSessionBootstrap
import com.chaikasoft.app.domain.sealed.LogoutResult.ActiveShiftExists
import com.chaikasoft.app.domain.sealed.LogoutResult.Failure
import com.chaikasoft.app.domain.sealed.LogoutResult.Success
import com.chaikasoft.app.domain.usecases.CompleteAuthorizationFlowUseCase
import com.chaikasoft.app.domain.usecases.GetAccessTokenUseCase
import com.chaikasoft.app.domain.usecases.LogoutUseCase
import com.chaikasoft.app.domain.usecases.StartAuthorizationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ---- Машина состояний авторизации ----
sealed interface AuthState {
    data object Checking : AuthState
    data object Unauthenticated : AuthState
    data object Authenticated : AuthState
}

// ---- UI-состояние для экрана(ов) авторизации/профиля ----
data class AuthUiState(
    val state: AuthState = AuthState.Checking,
    val errorMessage: String? = null,
    val showLogoutErrorDialog: Boolean = false,
    val showActiveShiftDialog: Boolean = false,
    val logoutErrorMessage: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val getAccessTokenUseCase: GetAccessTokenUseCase,
    private val completeAuthorizationFlowUseCase: CompleteAuthorizationFlowUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val startAuthorizationUseCase: StartAuthorizationUseCase,
    private val authSessionBootstrap: AuthSessionBootstrap
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        Log.i("AuthViewModel", "init -> checkAuthStatus")
        checkAuthStatus()
    }

    // -------- Публичные API --------

    /** Запускает внешнюю активити/интент авторизации. Состояние тут не меняем. */
    fun startAuth(): Intent {
        _uiState.update { it.copy(errorMessage = null) }
        return startAuthorizationUseCase()
    }

    /** Обрабатывает результат авторизации. Корень приложения сам переключит граф по state. */
    fun handleAuthResult(intent: Intent) {
        viewModelScope.launch {
            setState(AuthState.Checking)
            runCatching {
                completeAuthorizationFlowUseCase(intent)
            }.onSuccess { (_, conductor) ->
                if (conductor.id != null) {
                    setState(AuthState.Authenticated)
                    Log.i("AuthViewModel", "Authorization completed, state=Authenticated")
                } else {
                    setState(AuthState.Unauthenticated)
                    _uiState.update { it.copy(errorMessage = "Conductor data not saved properly") }
                    Log.w("AuthViewModel", "Authorization completed but conductor.id == null")
                }
            }.onFailure { e ->
                if (e is CancellationException) throw e
                // best-effort очистка, не эскалируем ошибку этой операции
                runCatching { logoutUseCase() }
                    .onFailure {
                        Log.w("AuthViewModel", "logoutUseCase after auth failure failed", it)
                    }

                setState(AuthState.Unauthenticated)
                _uiState.update { it.copy(errorMessage = e.message ?: "Authorization failed") }
                Log.e("AuthViewModel", "Authorization flow failed", e)
            }
        }
    }

    /** Проверка токена при старте приложения. */
    private fun checkAuthStatus() {
        viewModelScope.launch {
            setState(AuthState.Checking)
            runCatching {
                authSessionBootstrap.bootstrapIfNeeded()
                getAccessTokenUseCase()
            }.onSuccess { token ->
                if (token != null) {
                    setState(AuthState.Authenticated)
                    Log.i("AuthViewModel", "Token found -> Authenticated")
                } else {
                    setState(AuthState.Unauthenticated)
                    Log.i("AuthViewModel", "No token -> Unauthenticated")
                }
            }.onFailure { e ->
                if (e is CancellationException) throw e
                setState(AuthState.Unauthenticated)
                _uiState.update {
                    it.copy(errorMessage = e.message ?: "Authorization check failed")
                }
                Log.e("AuthViewModel", "checkAuthStatus failed", e)
            }
        }
    }

    /** Логаут пользователя. Ветвление по результату (shift/ошибка) сохраняем. */
    fun logout() {
        viewModelScope.launch {
            runCatching {
                logoutUseCase()
            }.onSuccess { result ->
                when (result) {
                    Success -> {
                        setState(AuthState.Unauthenticated)
                        _uiState.update {
                            it.copy(
                                showLogoutErrorDialog = false,
                                showActiveShiftDialog = false,
                                logoutErrorMessage = null
                            )
                        }
                        Log.i("AuthViewModel", "Logout success -> Unauthenticated")
                    }

                    ActiveShiftExists -> {
                        _uiState.update {
                            it.copy(
                                showActiveShiftDialog = true,
                                showLogoutErrorDialog = false
                            )
                        }
                        Log.i("AuthViewModel", "Logout blocked: active shift exists")
                    }

                    is Failure -> {
                        _uiState.update {
                            it.copy(
                                showLogoutErrorDialog = true,
                                logoutErrorMessage = result.reason,
                                showActiveShiftDialog = false
                            )
                        }
                        Log.w("AuthViewModel", "Logout failure: ${result.reason}")
                    }
                }
            }.onFailure { e ->
                if (e is CancellationException) throw e
                _uiState.update {
                    it.copy(
                        showLogoutErrorDialog = true,
                        logoutErrorMessage = e.message ?: "Logout failed",
                        showActiveShiftDialog = false
                    )
                }
                Log.e("AuthViewModel", "Logout exception", e)
            }
        }
    }

    fun dismissLogoutErrorDialog() {
        _uiState.update { it.copy(showLogoutErrorDialog = false, logoutErrorMessage = null) }
    }

    fun dismissActiveShiftDialog() {
        _uiState.update { it.copy(showActiveShiftDialog = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    // -------- Вспомогательное --------
    private fun setState(newState: AuthState) {
        _uiState.update { it.copy(state = newState) }
    }
}
