package com.example.chaika.ui.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chaika.domain.models.ConductorDomain
import com.example.chaika.domain.usecases.GetAccessTokenUseCase
import com.example.chaika.domain.usecases.GetAllConductorsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ConductorViewModel @Inject constructor(
    private val getAccessTokenUseCase: GetAccessTokenUseCase,
    private val getAllConductorsUseCase: GetAllConductorsUseCase,
) : ViewModel() {

    private val _conductor: MutableStateFlow<ConductorDomain?> = MutableStateFlow<ConductorDomain?>(null)
    val conductor: StateFlow<ConductorDomain?> = _conductor.asStateFlow()

    private val _allConductors: MutableStateFlow<List<ConductorDomain?>> = MutableStateFlow<List<ConductorDomain?>>(emptyList())
    val allConductors: StateFlow<List<ConductorDomain?>> = _allConductors.asStateFlow()

    fun getAllConductors(): Flow<List<ConductorDomain>> = getAllConductorsUseCase()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        loadConductorsData()
    }

    // TODO(УДАЛИТЬ ПОСЛЕ СОЗДАНИЯ ДИАЛОГА)
    private fun loadConductorsData() {
        viewModelScope.launch {
            getAllConductorsUseCase()
                .distinctUntilChanged()
                .collect { conductors ->
                    // Обновляем список
                    _allConductors.value = conductors

                    // Обновляем первого кондуктора (если список изменился)
                    _conductor.value = conductors.firstOrNull()
                }
        }
    }

    // Дополнительные методы по необходимости
    fun refresh() {
        loadConductorsData()
    }

    suspend fun getToken() {
        try {
            getAccessTokenUseCase()
        } catch (e: Exception) {
            Log.e("ConductorViewModel", "Error fetching token:  [ [${e.message}]", e)
        }
    }
}