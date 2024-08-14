package com.example.chaika.services

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chaika.dataBase.entities.Action
import com.example.chaika.dataBase.models.ActionRepository
import kotlinx.coroutines.launch

class ActionViewModel(private val repository: ActionRepository) : ViewModel() {

    fun getActionsByTripId(tripId: Int): LiveData<List<Action>> {
        return repository.getActionsByTripId(tripId)
    }

    fun insert(action: Action) {
        viewModelScope.launch {
            repository.insert(action)
        }
    }

    fun deleteActionsByTripId(tripId: Int) {
        viewModelScope.launch {
            repository.deleteActionsByTripId(tripId)
        }
    }
}
