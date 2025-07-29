package com.example.chaika.ui.viewModels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor( ) : ViewModel() {
    private val _uiState = MutableStateFlow<ScreenState>(ScreenState.Empty)
    private val _isLoading = MutableStateFlow(false)
    val uiState: StateFlow<ScreenState> = _uiState.asStateFlow()
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    sealed class ScreenState {
        object Empty : ScreenState()
        object ProductList : ScreenState()
        object Package: ScreenState()
        object Cart : ScreenState()
        object Error : ScreenState()
    }
}