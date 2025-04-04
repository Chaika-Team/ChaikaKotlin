package com.example.chaika.ui.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chaika.domain.usecases.GetAccessTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


class AuthCheckViewModel @Inject constructor(
    private val getAccessTokenUseCase: GetAccessTokenUseCase,
) : ViewModel() {

    private val _token = MutableLiveData<String?>()
    val token: LiveData<String?> get() = _token

    init {
        viewModelScope.launch {
            // Получаем сохранённый токен (если он есть)
            _token.value = getAccessTokenUseCase()
        }
    }
}
