package com.example.chaika.ui.viewModels

import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chaika.domain.usecases.CompleteAuthorizationFlowUseCase
import com.example.chaika.domain.usecases.GetAccessTokenUseCase
import com.example.chaika.domain.usecases.LogoutUseCase
import com.example.chaika.domain.usecases.StartAuthorizationUseCase
import com.example.chaika.domain.models.ConductorDomain
import com.example.chaika.domain.usecases.AuthorizeAndSaveConductorUseCase
import com.example.chaika.domain.usecases.GetAllConductorsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val getAccessTokenUseCase: GetAccessTokenUseCase,
    private val startAuthorizationUseCase: StartAuthorizationUseCase,
    private val completeAuthorizationFlowUseCase: CompleteAuthorizationFlowUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val authorizeAndSaveConductorUseCase: AuthorizeAndSaveConductorUseCase,
    private val getAllConductorsUseCase: GetAllConductorsUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "AuthViewModel"
    }

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _conductorState = MutableStateFlow<ConductorDomain?>(null)
    val conductorState: StateFlow<ConductorDomain?> = _conductorState.asStateFlow()

    private val _conductors = MutableStateFlow<List<ConductorDomain>>(emptyList())
    val conductors: StateFlow<List<ConductorDomain>> = _conductors.asStateFlow()


    init {
        Log.d(TAG, "AuthViewModel initialized")
        checkInitialAuthState()
    }

    private fun checkInitialAuthState() {
        Log.d(TAG, "Checking initial auth state...")
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isCheckingAuth = true, isLoading = false)
            try {
                val token = getAccessTokenUseCase()
                val hasToken = token != null
                Log.d(TAG, "Token check result: hasToken = $hasToken")
                _uiState.value = _uiState.value.copy(
                    isCheckingAuth = false,
                    isAuthenticated = hasToken
                )
                if (hasToken) {
                    try {
                        val conductorFromDb = authorizeAndSaveConductorUseCase(token!!)
                        _conductorState.value = conductorFromDb
                        Log.d(TAG, "Conductor loaded and saved locally after token check (id=${conductorFromDb.id})")
                    } catch (e: Exception) {
                        Log.e(TAG, "Error fetching/saving conductor after token check", e)
                        _conductorState.value = null
                    }
                } else {
                    _conductorState.value = null
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error checking initial auth state", e)
                _uiState.value = _uiState.value.copy(
                    isCheckingAuth = false,
                    isAuthenticated = false
                )
                _conductorState.value = null
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
                isCheckingAuth = false,
                errorMessage = null
            )
            try {
                val (_, conductor) = completeAuthorizationFlowUseCase(intent)
                Log.d(TAG, "Auth flow completed successfully")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isAuthenticated = true,
                    isCheckingAuth = false,
                    errorMessage = null
                )
                _conductorState.value = conductor
                Log.d(TAG, "Conductor loaded and saved locally after auth (id=${conductor.id})")
            } catch (e: Exception) {
                Log.e(TAG, "Auth flow failed", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isAuthenticated = false,
                    errorMessage = "Login error: ${e.message}"
                )
                _conductorState.value = null
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
            try {
                logoutUseCase()
                Log.d(TAG, "Logout use case completed successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Logout failed", e)
            } finally {
                _uiState.value = _uiState.value.copy(isAuthenticated = false)
                _conductorState.value = null
            }
        }
    }

    fun loadConductors() {
        viewModelScope.launch {
            try {
                getAllConductorsUseCase().collectLatest { _conductors.value = it }
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Error loading conductors:  [ [${e.message}]", e)
            }
        }
    }
}

data class AuthUiState(
    val isCheckingAuth: Boolean = false,
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val errorMessage: String? = null
)