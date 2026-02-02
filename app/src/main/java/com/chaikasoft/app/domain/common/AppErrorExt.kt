package com.chaikasoft.app.domain.common

fun AppError.isRetryable(): Boolean = when (this) {
    AppError.Network, AppError.Timeout -> true
    is AppError.Unauthorized -> false
    is AppError.Http -> when (this.code) {
        408, 429 -> true
        else -> this.code >= 500
    }
    is AppError.Serialization -> false
    is AppError.Unknown -> false
}
