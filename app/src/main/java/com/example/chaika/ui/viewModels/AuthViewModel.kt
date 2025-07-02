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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val getAccessTokenUseCase: GetAccessTokenUseCase,
    private val startAuthorizationUseCase: StartAuthorizationUseCase,
    private val completeAuthorizationFlowUseCase: CompleteAuthorizationFlowUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "AuthViewModel"
    }

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        Log.d(TAG, "AuthViewModel initialized")
        checkInitialAuthState()
    }

    private fun checkInitialAuthState() {
        Log.d(TAG, "Checking initial auth state...")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCheckingAuth = true)

            try {
                val hasToken = getAccessTokenUseCase() != null
                Log.d(TAG, "Token check result: hasToken = $hasToken")

                _uiState.value = _uiState.value.copy(
                    isCheckingAuth = false,
                    isAuthenticated = hasToken
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error checking initial auth state", e)
                _uiState.value = _uiState.value.copy(
                    isCheckingAuth = false,
                    isAuthenticated = false
                )
            }
        }
    }

    fun startAuth(): Intent {
        Log.d(TAG, "Starting auth flow...")
        val intent = startAuthorizationUseCase()
        Log.d(TAG, "Auth intent created")
        return intent
    }

    fun handleAuthResult(intent: Intent) {
        Log.d(TAG, "Handling auth result...")

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                completeAuthorizationFlowUseCase(intent)
                Log.d(TAG, "Auth flow completed successfully")
                Log.d(TAG, "Token received")

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isAuthenticated = true,
                    errorMessage = null
                )
            } catch (e: Exception) {
                Log.e(TAG, "Auth flow failed", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isAuthenticated = false,
                    errorMessage = "Login error: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun onNavigationHandled() {
        Log.d(TAG, "Navigation handled")
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAuthenticated = false)
            logoutUseCase()
        }
    }
}

data class AuthUiState(
    val isCheckingAuth: Boolean = false,
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val errorMessage: String? = null
)