package com.chaikasoft.app.data.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.chaikasoft.app.domain.models.settings.AppLanguage
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class AppCompatLanguageRepository @Inject constructor() : LanguageRepositoryInterface {
    private val languageState = MutableStateFlow(AppLanguage.SYSTEM)

    override val language: StateFlow<AppLanguage> = languageState.asStateFlow()

    override fun refreshLanguage(): AppLanguage {
        val currentLanguage = readLanguage()
        languageState.value = currentLanguage
        return currentLanguage
    }

    override fun setLanguage(language: AppLanguage) {
        AppCompatDelegate.setApplicationLocales(language.toLocaleListCompat())
        languageState.value = language
    }

    private fun readLanguage(): AppLanguage = AppLanguage.fromLanguageTag(
        AppCompatDelegate.getApplicationLocales()[0]?.toLanguageTag()
    )
}

fun AppLanguage.toLocaleListCompat(): LocaleListCompat =
    languageTag?.let(LocaleListCompat::forLanguageTags)
        ?: LocaleListCompat.getEmptyLocaleList()
