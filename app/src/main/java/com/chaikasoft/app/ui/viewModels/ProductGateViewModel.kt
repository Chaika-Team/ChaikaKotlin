package com.chaikasoft.app.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaikasoft.app.domain.usecases.HasAnyPackageItemsOnceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductGateViewModel @Inject constructor(
    private val hasAnyOnce: HasAnyPackageItemsOnceUseCase
) : ViewModel() {

    enum class Target { PACKAGE, ENTRY }

    sealed interface ProductGateUiState {
        data object Loading : ProductGateUiState
        data class Resolved(val target: Target) : ProductGateUiState
    }

    private val _uiState = MutableStateFlow<ProductGateUiState>(ProductGateUiState.Loading)
    val uiState: StateFlow<ProductGateUiState> = _uiState.asStateFlow()

    fun resolveTarget() {
        viewModelScope.launch {
            _uiState.value = ProductGateUiState.Loading
            val target = runCatching {
                if (hasAnyOnce()) Target.PACKAGE else Target.ENTRY
            }.getOrElse { Target.ENTRY }
            _uiState.value = ProductGateUiState.Resolved(target)
        }
    }
}
