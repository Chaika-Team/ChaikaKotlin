package com.chaikasoft.app.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaikasoft.app.domain.usecases.GetActiveShiftUseCase
import com.chaikasoft.app.startup.PostAuthStartupSeam
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class MainNavigationViewModel @Inject constructor(
    getActiveShiftUseCase: GetActiveShiftUseCase,
    private val postAuthStartupSeam: PostAuthStartupSeam
) : ViewModel() {

    private var silentRefreshRunning = false

    val hasActiveShift: StateFlow<Boolean> =
        getActiveShiftUseCase()
            .map { shift -> shift != null }
            .catch { emit(false) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = false
            )

    fun refreshAuthenticatedAppSilently() {
        if (silentRefreshRunning) return

        silentRefreshRunning = true
        viewModelScope.launch {
            try {
                runCatching { postAuthStartupSeam.prepareForAuthenticatedApp() }
                    .onFailure { e ->
                        if (e is CancellationException) throw e
                        Log.w(TAG, "Silent post-auth refresh failed", e)
                    }
            } finally {
                silentRefreshRunning = false
            }
        }
    }

    private companion object {
        const val TAG = "MainNavigationViewModel"
    }
}
