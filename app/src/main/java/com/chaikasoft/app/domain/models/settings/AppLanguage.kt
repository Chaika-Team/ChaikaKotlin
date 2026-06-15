package com.chaikasoft.app.domain.models.settings

import java.util.Locale

enum class AppLanguage(val languageTag: String?) {
    SYSTEM(null),
    RU("ru"),
    EN("en");

    companion object {
        fun fromLanguageTag(languageTag: String?): AppLanguage {
            if (languageTag.isNullOrBlank()) {
                return SYSTEM
            }

            val language = Locale.forLanguageTag(languageTag).language
            return entries.firstOrNull { it.languageTag == language } ?: SYSTEM
        }
    }
}
