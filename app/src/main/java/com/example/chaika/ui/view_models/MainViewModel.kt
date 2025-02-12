package com.example.chaika.ui.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chaika.domain.usecases.GetAccessTokenUseCase
import com.example.chaika.domain.usecases.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getAccessTokenUseCase: GetAccessTokenUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _accessToken = MutableLiveData<String?>()
    val accessToken: LiveData<String?> = _accessToken

    private val _logoutSuccess = MutableLiveData<Boolean>()
    val logoutSuccess: LiveData<Boolean> = _logoutSuccess

    init {
        viewModelScope.launch {
            _accessToken.value = getAccessTokenUseCase()
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _accessToken.postValue(null)
            _logoutSuccess.postValue(true)
        }
    }
}
