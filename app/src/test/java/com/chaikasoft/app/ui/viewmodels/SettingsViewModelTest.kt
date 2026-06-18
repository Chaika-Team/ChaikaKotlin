package com.chaikasoft.app.ui.viewmodels

import app.cash.turbine.test
import com.chaikasoft.app.data.settings.LanguageRepositoryInterface
import com.chaikasoft.app.data.settings.SettingsRepositoryInterface
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
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState combines settings and language`() = runTest {
        val settingsRepository = FakeSettingsRepository()
        val languageRepository = FakeLanguageRepository()
        val viewModel = viewModel(settingsRepository, languageRepository)

        viewModel.uiState.test {
            awaitItem() shouldBe SettingsUiState()

            settingsRepository.setDarkThemeEnabled(true)
            languageRepository.setLanguage(AppLanguage.EN)

            awaitItem() shouldBe SettingsUiState(
                settings = AppSettings(darkThemeEnabled = true),
                language = AppLanguage.SYSTEM
            )
            awaitItem() shouldBe SettingsUiState(
                settings = AppSettings(darkThemeEnabled = true),
                language = AppLanguage.EN
            )
        }
    }

    @Test
    fun `viewModel writes settings through use cases`() = runTest {
        val settingsRepository = FakeSettingsRepository()
        val languageRepository = FakeLanguageRepository()
        val viewModel = viewModel(settingsRepository, languageRepository)

        viewModel.onNotificationsEnabledChange(false)
        viewModel.onSoundEnabledChange(false)
        viewModel.onVibrationEnabledChange(false)
        viewModel.onDarkThemeEnabledChange(true)
        viewModel.onAutoSyncEnabledChange(false)
        advanceUntilIdle()

        settingsRepository.settings.value shouldBe AppSettings(
            notificationsEnabled = false,
            soundEnabled = false,
            vibrationEnabled = false,
            darkThemeEnabled = true,
            autoSyncEnabled = false
        )
    }

    @Test
    fun `viewModel changes language through use case`() = runTest {
        val languageRepository = FakeLanguageRepository()
        val viewModel = viewModel(FakeSettingsRepository(), languageRepository)

        viewModel.onLanguageChange(AppLanguage.RU)

        languageRepository.language.value shouldBe AppLanguage.RU
    }

    @Test
    fun `viewModel refreshes language through use case`() = runTest {
        val languageRepository = FakeLanguageRepository()
        val viewModel = viewModel(FakeSettingsRepository(), languageRepository)

        languageRepository.storedLanguage = AppLanguage.EN
        viewModel.refreshLanguage()

        languageRepository.language.value shouldBe AppLanguage.EN
    }

    private fun viewModel(
        settingsRepository: SettingsRepositoryInterface,
        languageRepository: LanguageRepositoryInterface
    ): SettingsViewModel =
        SettingsViewModel(
            observeSettings = ObserveSettingsUseCase(settingsRepository),
            observeAppLanguage = ObserveAppLanguageUseCase(languageRepository),
            refreshAppLanguage = RefreshAppLanguageUseCase(languageRepository),
            setNotificationsEnabled = SetNotificationsEnabledUseCase(settingsRepository),
            setSoundEnabled = SetSoundEnabledUseCase(settingsRepository),
            setVibrationEnabled = SetVibrationEnabledUseCase(settingsRepository),
            setDarkThemeEnabled = SetDarkThemeEnabledUseCase(settingsRepository),
            setAutoSyncEnabled = SetAutoSyncEnabledUseCase(settingsRepository),
            setAppLanguage = SetAppLanguageUseCase(languageRepository)
        )

    private class FakeSettingsRepository : SettingsRepositoryInterface {
        override val settings = MutableStateFlow(AppSettings())

        override suspend fun setNotificationsEnabled(enabled: Boolean) {
            settings.value = settings.value.copy(notificationsEnabled = enabled)
        }

        override suspend fun setSoundEnabled(enabled: Boolean) {
            settings.value = settings.value.copy(soundEnabled = enabled)
        }

        override suspend fun setVibrationEnabled(enabled: Boolean) {
            settings.value = settings.value.copy(vibrationEnabled = enabled)
        }

        override suspend fun setDarkThemeEnabled(enabled: Boolean) {
            settings.value = settings.value.copy(darkThemeEnabled = enabled)
        }

        override suspend fun setAutoSyncEnabled(enabled: Boolean) {
            settings.value = settings.value.copy(autoSyncEnabled = enabled)
        }
    }

    private class FakeLanguageRepository : LanguageRepositoryInterface {
        override val language = MutableStateFlow(AppLanguage.SYSTEM)
        var storedLanguage = AppLanguage.SYSTEM

        override fun refreshLanguage(): AppLanguage {
            language.value = storedLanguage
            return storedLanguage
        }

        override fun setLanguage(language: AppLanguage) {
            storedLanguage = language
            this.language.value = language
        }
    }
}
