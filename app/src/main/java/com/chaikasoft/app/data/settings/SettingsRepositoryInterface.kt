package com.chaikasoft.app.data.settings

import com.chaikasoft.app.domain.models.settings.AppSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepositoryInterface {
    val settings: Flow<AppSettings>

    suspend fun setNotificationsEnabled(enabled: Boolean)
    suspend fun setSoundEnabled(enabled: Boolean)
    suspend fun setVibrationEnabled(enabled: Boolean)
    suspend fun setDarkThemeEnabled(enabled: Boolean)
    suspend fun setAutoSyncEnabled(enabled: Boolean)
}
