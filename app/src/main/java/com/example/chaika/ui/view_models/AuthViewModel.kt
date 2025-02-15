package com.example.chaika.ui.view_models

import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chaika.domain.usecases.AuthorizeAndSaveConductorUseCase
import com.example.chaika.domain.usecases.HandleAuthorizationResponseUseCase
import com.example.chaika.domain.usecases.StartAuthorizationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val startAuthorizationUseCase: StartAuthorizationUseCase,
    private val handleAuthorizationResponseUseCase: HandleAuthorizationResponseUseCase,
    private val authorizeAndSaveConductorUseCase: AuthorizeAndSaveConductorUseCase
) : ViewModel() {

    private val _accessToken = MutableLiveData<String>()
    val accessToken: LiveData<String> = _accessToken

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun getAuthIntent(): Intent {
        val intent = startAuthorizationUseCase()
        Log.d("AuthViewModel", "getAuthIntent() returned intent: $intent")
        return intent
    }

    fun processDeepLink(intent: Intent) {
        Log.d("AuthViewModel", "processDeepLink called with intent: $intent, data: ${intent.data}")
        viewModelScope.launch {
            try {
                Log.d(
                    "AuthViewModel",
                    "Calling handleAuthorizationResponseUseCase with intent: $intent"
                )
                val token = handleAuthorizationResponseUseCase(intent)
                Log.d("AuthViewModel", "Received token: $token")
                _accessToken.postValue(token)
                Log.d(
                    "AuthViewModel",
                    "Calling authorizeAndSaveConductorUseCase with token: $token"
                )
                val conductor = authorizeAndSaveConductorUseCase(token)
                Log.d("AuthViewModel", "Conductor data saved: $conductor")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error in processDeepLink: ${e.message}", e)
                _error.postValue(e.message ?: "Unknown error")
            }
        }
    }
}
