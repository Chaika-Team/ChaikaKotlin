package com.chaikasoft.app.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.chaikasoft.app.domain.models.settings.AppSettings
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class SettingsRepositoryTest {

    @Test
    fun `settings returns default values when datastore is empty`() = runTest {
        val repository = repository()

        repository.settings.first() shouldBe AppSettings()
    }

    @Test
    fun `settings persists each option without changing unrelated values`() = runTest {
        val repository = repository()

        repository.setNotificationsEnabled(false)
        repository.setSoundEnabled(false)
        repository.setVibrationEnabled(false)
        repository.setDarkThemeEnabled(true)
        repository.setAutoSyncEnabled(false)

        repository.settings.first() shouldBe AppSettings(
            notificationsEnabled = false,
            soundEnabled = false,
            vibrationEnabled = false,
            darkThemeEnabled = true,
            autoSyncEnabled = false
        )
    }

    @Test
    fun `settings preserves existing values when only one option changes`() = runTest {
        val repository = repository()

        repository.setNotificationsEnabled(false)
        repository.setDarkThemeEnabled(true)
        repository.setNotificationsEnabled(true)

        repository.settings.first() shouldBe AppSettings(
            notificationsEnabled = true,
            darkThemeEnabled = true
        )
    }

    private fun repository(): SettingsRepository =
        SettingsRepository(InMemoryPreferencesDataStore())

    private class InMemoryPreferencesDataStore : DataStore<Preferences> {
        private val state = MutableStateFlow(emptyPreferences())

        override val data: Flow<Preferences> = state

        override suspend fun updateData(
            transform: suspend (t: Preferences) -> Preferences
        ): Preferences {
            val updated = transform(state.value)
            state.value = updated
            return updated
        }
    }
}
