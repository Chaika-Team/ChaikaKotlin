package com.example.chaika.ui.viewModels

import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chaika.domain.usecases.CompleteAuthorizationFlowUseCase
import com.example.chaika.domain.usecases.StartAuthorizationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel
    @Inject
    constructor(
        private val startAuthorizationUseCase: StartAuthorizationUseCase,
        private val completeAuthorizationFlowUseCase: CompleteAuthorizationFlowUseCase,
    ) : ViewModel() {
        private val _accessToken = MutableLiveData<String>()
        val accessToken: LiveData<String> = _accessToken

        private val _error = MutableLiveData<String>()
        val error: LiveData<String> = _error

        /** Возвращает Intent для запуска OAuth‑авторизации через ActivityResultLauncher */
        fun getAuthIntent(): Intent {
            val intent = startAuthorizationUseCase()
            Log.d("AuthViewModel", "getAuthIntent() returned intent: $intent")
            return intent
        }

        /**
         * Обрабатывает результат OAuth авторизации из deep link.
         * Запускает CompleteAuthorizationFlowUseCase и публикует результат или ошибку в LiveData.
         */
        fun processDeepLink(intent: Intent) {
            Log.d("AuthViewModel", "processDeepLink called with intent=$intent, data=${intent.data}")
            viewModelScope.launch {
                try {
                    Log.d("AuthViewModel", "Starting completeAuthorizationFlowUseCase")
                    val (token, conductor) = completeAuthorizationFlowUseCase(intent)
                    Log.d(
                        "AuthViewModel",
                        "Authorization successful — token=$token, conductor=$conductor",
                    )
                    _accessToken.postValue(token)
                } catch (e: Exception) {
                    Log.e("AuthViewModel", "Error in processDeepLink: ${e.message}", e)
                    _error.postValue(e.message ?: "Unknown authorization error")
                }
            }
        }
    }
