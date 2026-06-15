package com.chaikasoft.app.domain.usecases

import com.chaikasoft.app.data.settings.LanguageRepositoryInterface
import com.chaikasoft.app.data.settings.SettingsRepositoryInterface
import com.chaikasoft.app.domain.models.settings.AppLanguage
import com.chaikasoft.app.domain.models.settings.AppSettings
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveSettingsUseCase @Inject constructor(
    private val repository: SettingsRepositoryInterface
) {
    operator fun invoke(): Flow<AppSettings> = repository.settings
}

class SetNotificationsEnabledUseCase @Inject constructor(
    private val repository: SettingsRepositoryInterface
) {
    suspend operator fun invoke(enabled: Boolean) = repository.setNotificationsEnabled(enabled)
}

class SetSoundEnabledUseCase @Inject constructor(
    private val repository: SettingsRepositoryInterface
) {
    suspend operator fun invoke(enabled: Boolean) = repository.setSoundEnabled(enabled)
}

class SetVibrationEnabledUseCase @Inject constructor(
    private val repository: SettingsRepositoryInterface
) {
    suspend operator fun invoke(enabled: Boolean) = repository.setVibrationEnabled(enabled)
}

class SetDarkThemeEnabledUseCase @Inject constructor(
    private val repository: SettingsRepositoryInterface
) {
    suspend operator fun invoke(enabled: Boolean) = repository.setDarkThemeEnabled(enabled)
}

class SetAutoSyncEnabledUseCase @Inject constructor(
    private val repository: SettingsRepositoryInterface
) {
    suspend operator fun invoke(enabled: Boolean) = repository.setAutoSyncEnabled(enabled)
}

class ObserveAppLanguageUseCase @Inject constructor(
    private val repository: LanguageRepositoryInterface
) {
    operator fun invoke(): Flow<AppLanguage> = repository.language
}

class RefreshAppLanguageUseCase @Inject constructor(
    private val repository: LanguageRepositoryInterface
) {
    operator fun invoke(): AppLanguage = repository.refreshLanguage()
}

class SetAppLanguageUseCase @Inject constructor(
    private val repository: LanguageRepositoryInterface
) {
    operator fun invoke(language: AppLanguage) = repository.setLanguage(language)
}
