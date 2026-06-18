package com.chaikasoft.app.data.settings

import com.chaikasoft.app.domain.models.settings.AppLanguage
import kotlinx.coroutines.flow.Flow

interface LanguageRepositoryInterface {
    val language: Flow<AppLanguage>

    fun refreshLanguage(): AppLanguage
    fun setLanguage(language: AppLanguage)
}
