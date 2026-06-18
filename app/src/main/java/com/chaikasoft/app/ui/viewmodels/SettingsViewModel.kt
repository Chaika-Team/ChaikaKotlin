package com.chaikasoft.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaikasoft.app.domain.models.settings.AppLanguage
import com.chaikasoft.app.domain.models.settings.AppSettings
import com.chaikasoft.app.domain.usecases.ObserveAppLanguageUseCase
import com.chaikasoft.app.domain.usecases.ObserveSettingsUseCase
import com.chaikasoft.app.domain.usecases.RefreshAppLanguageUseCase
import com.chaikasoft.app.domain.usecases.SetAppLanguageUseCase
import com.chaikasoft.app.domain.usecases.SetAutoSyncEnabledUseCase
import com.chaikasoft.app.domain.usecases.SetDarkThemeEnabledUseCase
import com.chaikasoft.app.domain.usecases.SetNotificationsEnabledUseCase
import com.chaikasoft.app.domain.usecases.SetSoundEnabledUseCase
import com.chaikasoft.app.domain.usecases.SetVibrationEnabledUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val settings: AppSettings = AppSettings(),
    val language: AppLanguage = AppLanguage.SYSTEM
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    observeSettings: ObserveSettingsUseCase,
    observeAppLanguage: ObserveAppLanguageUseCase,
    private val refreshAppLanguage: RefreshAppLanguageUseCase,
    private val setNotificationsEnabled: SetNotificationsEnabledUseCase,
    private val setSoundEnabled: SetSoundEnabledUseCase,
    private val setVibrationEnabled: SetVibrationEnabledUseCase,
    private val setDarkThemeEnabled: SetDarkThemeEnabledUseCase,
    private val setAutoSyncEnabled: SetAutoSyncEnabledUseCase,
    private val setAppLanguage: SetAppLanguageUseCase
) : ViewModel() {

    init {
        refreshLanguage()
    }

    val uiState: StateFlow<SettingsUiState> =
        combine(
            observeSettings(),
            observeAppLanguage()
        ) { settings, language ->
            SettingsUiState(
                settings = settings,
                language = language
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SettingsUiState()
        )

    fun onNotificationsEnabledChange(enabled: Boolean) {
        viewModelScope.launch {
            setNotificationsEnabled(enabled)
        }
    }

    fun onSoundEnabledChange(enabled: Boolean) {
        viewModelScope.launch {
            setSoundEnabled(enabled)
        }
    }

    fun onVibrationEnabledChange(enabled: Boolean) {
        viewModelScope.launch {
            setVibrationEnabled(enabled)
        }
    }

    fun onDarkThemeEnabledChange(enabled: Boolean) {
        viewModelScope.launch {
            setDarkThemeEnabled(enabled)
        }
    }

    fun onAutoSyncEnabledChange(enabled: Boolean) {
        viewModelScope.launch {
            setAutoSyncEnabled(enabled)
        }
    }

    fun onLanguageChange(language: AppLanguage) {
        setAppLanguage(language)
    }

    fun refreshLanguage() {
        refreshAppLanguage()
    }
}
