package com.example.chaika.ui.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chaika.domain.models.ConductorDomain
import com.example.chaika.domain.usecases.GetAccessTokenUseCase
import com.example.chaika.domain.usecases.FetchConductorByTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getAccessTokenUseCase: GetAccessTokenUseCase,
    private val fetchConductorByTokenUseCase: FetchConductorByTokenUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScreenState>(ScreenState.Profile)
    val uiState: StateFlow<ScreenState> = _uiState.asStateFlow()

    private val _conductorState = MutableStateFlow<ConductorDomain?>(null)
    val conductorState: StateFlow<ConductorDomain?> = _conductorState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val token = getAccessTokenUseCase()
                if (token != null) {
                    val conductor = fetchConductorByTokenUseCase(token)
                    _conductorState.value = conductor
                } else {
                    _conductorState.value = null
                }
            } catch (e: Exception) {
                // TODO: Обработать ошибку загрузки данных проводника
                Log.e("ProfileViewModel", "Error loading conductor data", e)
                _conductorState.value = null
            }
        }
    }

    sealed class ScreenState {
        object Profile : ScreenState()
        object PersonalData : ScreenState()
        object Settings : ScreenState()
        object Faqs : ScreenState()
        object Feedback : ScreenState()
        object About : ScreenState()
        object Error : ScreenState()
    }
} 