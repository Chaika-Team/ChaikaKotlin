package com.example.chaika.ui.viewModels

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DeepLinkViewModel @Inject constructor() : ViewModel() {
    private val _deepLinkIntent = MutableLiveData<Intent?>()
    val deepLinkIntent: LiveData<Intent?> = _deepLinkIntent

    fun postDeepLink(intent: Intent) {
        _deepLinkIntent.value = intent
    }

    fun clearDeepLink() {
        _deepLinkIntent.value = null
    }
}
