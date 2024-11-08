package com.example.chaika.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chaika.domain.models.ConductorDomain
import com.example.chaika.domain.usecases.AddConductorUseCase
import com.example.chaika.domain.usecases.DeleteConductorUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConductorViewModel @Inject constructor(
    private val addConductorUseCase: AddConductorUseCase,
    private val deleteConductorUseCase: DeleteConductorUseCase
) : ViewModel() {

    fun addConductor(conductorDomain: ConductorDomain) {
        viewModelScope.launch {
            addConductorUseCase(conductorDomain)
        }
    }

    fun deleteConductor(conductorDomain: ConductorDomain) {
        viewModelScope.launch {
            deleteConductorUseCase(conductorDomain)
        }
    }
}
