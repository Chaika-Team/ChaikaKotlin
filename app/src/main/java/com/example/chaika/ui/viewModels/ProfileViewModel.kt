package com.example.chaika.ui.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chaika.domain.models.ConductorDomain
import com.example.chaika.domain.usecases.GetAllConductorsUseCase
import com.example.chaika.domain.usecases.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getAllConductorsUseCase: GetAllConductorsUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {

    private val _conductor = MutableLiveData<ConductorDomain?>()
    val conductor: LiveData<ConductorDomain?> get() = _conductor

    private val _logoutSuccess = MutableLiveData<Boolean>()
    val logoutSuccess: LiveData<Boolean> get() = _logoutSuccess

    init {
        viewModelScope.launch {
            getAllConductorsUseCase().collect { list ->
                // Берем первый элемент, если список не пустой
                _conductor.value = list.firstOrNull()
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _logoutSuccess.postValue(true)
        }
    }
}
