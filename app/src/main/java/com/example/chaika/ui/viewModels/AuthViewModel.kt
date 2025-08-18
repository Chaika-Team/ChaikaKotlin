package com.example.chaika.ui.viewModels

import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chaika.domain.usecases.CompleteAuthorizationFlowUseCase
import com.example.chaika.domain.usecases.GetAccessTokenUseCase
import com.example.chaika.domain.usecases.LogoutUseCase
import com.example.chaika.domain.usecases.StartAuthorizationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val getAccessTokenUseCase: GetAccessTokenUseCase,
    private val completeAuthorizationFlowUseCase: CompleteAuthorizationFlowUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val startAuthorizationUseCase: StartAuthorizationUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        Log.i("AuthViewModel", "AuthViewModel launched")
        checkAuthStatus()
    }

    fun startAuth(): Intent {
        Log.i("AuthViewModel", "startAuth")
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        return startAuthorizationUseCase()
    }

    fun handleAuthResult(intent: Intent) {
        Log.i("AuthViewModel", "handleAuthResult")
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val (_, conductor) = completeAuthorizationFlowUseCase(intent)
                if (conductor.id != null) {
                    onAuthenticated()
                    Log.i("AuthViewModel", "finished handleAuthResult")
                } else {
                    throw IllegalStateException("Conductor data not saved properly")
                }
            } catch (e: Exception) {
                onException(e)
            }
        }
    }

    private fun checkAuthStatus() {
        Log.i("AuthViewModel", "checkAuthStatus")
        viewModelScope.launch {
            try {
                val token = getAccessTokenUseCase()
                if (token != null) {
                    onAuthenticated()
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isAuthenticated = false
                        )
                    }
                }
            } catch (e: Exception) {
                onException(e)
            }
        }
    }

    fun logout() {
        Log.i("AuthViewModel", "logout")
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                logoutUseCase()
                _uiState.update {
                    it.copy(
                        isAuthenticated = false,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                onLogoutException(e)
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun onNavigationHandled() {
        _uiState.update { it.copy(isAuthenticated = true) }
    }

    private fun onAuthenticated() {
        Log.i("AuthViewModel", "onAuthenticated")
        _uiState.update {
            it.copy(
                isAuthenticated = true,
                isLoading = false
            )
        }
        Log.i("AuthViewModel", "finished onAuthenticated")
    }

        private suspend fun onException(e: Exception) {
            Log.e("AuthViewModel", "Authorization flow failed", e)
            runCatching { logoutUseCase() }
                .onFailure { Log.w("AuthViewModel", "Logout after auth failure failed", it) }
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Authorization failed"
                )
            }
        }

    private fun onLogoutException(e: Exception) {
        _uiState.update {
            it.copy(
                isLoading = false,
                errorMessage = e.message ?: "Unknown logout exception"
            )
        }
    }
}

data class AuthUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val errorMessage: String? = null
)