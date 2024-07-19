package com.example.chaika.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.chaika.dataBase.models.ActionRepository

class ActionViewModelFactory(private val repository: ActionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ActionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ActionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
