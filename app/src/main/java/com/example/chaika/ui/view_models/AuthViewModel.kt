package com.example.chaika.ui.view_models

import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chaika.domain.usecases.HandleAuthorizationResponseUseCase
import com.example.chaika.domain.usecases.StartAuthorizationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val startAuthorizationUseCase: StartAuthorizationUseCase,
    private val handleAuthorizationResponseUseCase: HandleAuthorizationResponseUseCase
) : ViewModel() {

    private val _accessToken = MutableLiveData<String>()
    val accessToken: LiveData<String> = _accessToken

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    /** Возвращает Intent для запуска авторизации. */
    fun getAuthIntent(): Intent = startAuthorizationUseCase()

    /**
     * Обрабатывает deep link Intent, обменивая код авторизации на access token.
     */
    fun processDeepLink(intent: Intent) {
        viewModelScope.launch {
            try {
                val token = handleAuthorizationResponseUseCase(intent)
                _accessToken.postValue(token)
            } catch (e: Exception) {
                _error.postValue(e.message ?: "Unknown error")
                Log.e("AuthViewModel", "Error processing deep link", e)
            }
        }
    }

}
