package com.example.chaika.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chaika.domain.models.ConductorDomain
import com.example.chaika.domain.usecases.GetAllConductorsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getAllConductorsUseCase: GetAllConductorsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScreenState>(ScreenState.Profile)
    val uiState: StateFlow<ScreenState> = _uiState.asStateFlow()

    private val _conductorState = MutableStateFlow<ConductorDomain?>(null)
    val conductorState: StateFlow<ConductorDomain?> = _conductorState.asStateFlow()

    init {
        viewModelScope.launch {
            getAllConductorsUseCase().map { it.firstOrNull() }.collect {
                _conductorState.value = it
            }
        }
    }

    fun onPersonalDataClick() {
        _uiState.value = ScreenState.PersonalData
    }

    fun onSettingsClick() {
        _uiState.value = ScreenState.Settings
    }

    fun onFaqsClick() {
        _uiState.value = ScreenState.Faqs
    }

    fun onFeedbackClick() {
        _uiState.value = ScreenState.Feedback
    }

    fun onAboutClick() {
        _uiState.value = ScreenState.About
    }

    sealed class ScreenState {
        object Profile : ScreenState()
        object PersonalData : ScreenState()
        object Settings : ScreenState()
        object Faqs : ScreenState()
        object Feedback : ScreenState()
        object About : ScreenState()
    }
} 