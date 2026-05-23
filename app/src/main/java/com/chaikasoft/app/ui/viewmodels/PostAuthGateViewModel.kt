package com.chaikasoft.app.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaikasoft.app.startup.PostAuthStartupSeam
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class PostAuthGateViewModel @Inject constructor(
    private val postAuthStartupSeam: PostAuthStartupSeam
) : ViewModel() {

    sealed interface PostAuthGateUiState {
        data object Loading : PostAuthGateUiState
        data class Ready(val hadRefreshFailure: Boolean) : PostAuthGateUiState
    }

    private val _uiState = MutableStateFlow<PostAuthGateUiState>(PostAuthGateUiState.Loading)
    val uiState: StateFlow<PostAuthGateUiState> = _uiState.asStateFlow()

    private var started = false

    fun prepare() {
        if (started) return
        started = true

        viewModelScope.launch {
            val hadRefreshFailure = try {
                postAuthStartupSeam.prepareForAuthenticatedApp().hadRefreshFailure
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Post-auth startup crashed unexpectedly", e)
                true
            }

            _uiState.value = PostAuthGateUiState.Ready(hadRefreshFailure = hadRefreshFailure)
        }
    }

    private companion object {
        const val TAG = "PostAuthGateViewModel"
    }
}
