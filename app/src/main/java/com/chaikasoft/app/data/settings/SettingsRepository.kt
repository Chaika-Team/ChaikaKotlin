package com.chaikasoft.app.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.chaikasoft.app.domain.models.settings.AppSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "settings"
)

@Singleton
class SettingsRepository : SettingsRepositoryInterface {
    private val dataStore: DataStore<Preferences>

    override val settings: Flow<AppSettings>

    @Inject
    constructor(@ApplicationContext context: Context) {
        dataStore = context.settingsDataStore
        settings = buildSettingsFlow()
    }

    internal constructor(dataStore: DataStore<Preferences>) {
        this.dataStore = dataStore
        settings = buildSettingsFlow()
    }

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED] = enabled
        }
    }

    override suspend fun setSoundEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[SOUND_ENABLED] = enabled
        }
    }

    override suspend fun setVibrationEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[VIBRATION_ENABLED] = enabled
        }
    }

    override suspend fun setDarkThemeEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_THEME_ENABLED] = enabled
        }
    }

    override suspend fun setAutoSyncEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[AUTO_SYNC_ENABLED] = enabled
        }
    }

    private fun buildSettingsFlow(): Flow<AppSettings> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            AppSettings(
                notificationsEnabled = preferences[NOTIFICATIONS_ENABLED] ?: true,
                soundEnabled = preferences[SOUND_ENABLED] ?: true,
                vibrationEnabled = preferences[VIBRATION_ENABLED] ?: true,
                darkThemeEnabled = preferences[DARK_THEME_ENABLED] ?: false,
                autoSyncEnabled = preferences[AUTO_SYNC_ENABLED] ?: true
            )
        }

    private companion object {
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val DARK_THEME_ENABLED = booleanPreferencesKey("dark_theme_enabled")
        val AUTO_SYNC_ENABLED = booleanPreferencesKey("auto_sync_enabled")
    }
}
