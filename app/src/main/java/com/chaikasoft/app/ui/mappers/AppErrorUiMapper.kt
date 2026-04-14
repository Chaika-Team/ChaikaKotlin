package com.chaikasoft.app.ui.mappers

import androidx.annotation.StringRes
import com.chaikasoft.app.R
import com.chaikasoft.app.domain.common.AppError
import com.chaikasoft.app.domain.common.isRetryable

/**
 * Универсальный маппер AppError -> UI-представление (строка + retryable).
 * Используется во всех ViewModel/экранах для единообразной обработки ошибок.
 */
data class UiError(@StringRes val messageRes: Int, val retryable: Boolean)

object AppErrorUiMapper {
    fun map(error: AppError): UiError {
        val retryable = error.isRetryable()
        val messageRes = when (error) {
            AppError.Network, AppError.Timeout -> R.string.error_no_connection

            is AppError.Unauthorized -> R.string.error_unauthorized

            is AppError.Http -> R.string.error_try_later

            is AppError.Serialization,
            is AppError.Unknown -> R.string.error_try_later
        }
        return UiError(messageRes, retryable)
    }
}
