package com.example.chaika.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chaika.domain.models.ConductorDomain
import com.example.chaika.domain.usecases.GetAllConductorsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ConductorViewModel @Inject constructor(
    private val getAllConductorsUseCase: GetAllConductorsUseCase,
) : ViewModel() {

    private val _conductor: MutableStateFlow<ConductorDomain?> = MutableStateFlow<ConductorDomain?>(null)
    val conductor: StateFlow<ConductorDomain?> = _conductor.asStateFlow()

    private val _allConductors: MutableStateFlow<List<ConductorDomain>> = MutableStateFlow<List<ConductorDomain>>(emptyList())
    val allConductors: StateFlow<List<ConductorDomain>> = _allConductors.asStateFlow()

    init {
        viewModelScope.launch {
            getAllConductorsUseCase()
                .distinctUntilChanged()
                .collect { conductors ->
                    _allConductors.value = conductors
                }
        }
        viewModelScope.launch {
            allConductors.collect {
                    list -> _conductor.value = list.firstOrNull()
            }
        }
    }
}