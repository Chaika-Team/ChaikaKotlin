package com.chaikasoft.app.domain.common

fun AppError.isRetryable(): Boolean = when (this) {
    is AppError.Network,
    is AppError.Timeout -> true
    is AppError.Http -> when (this.code) {
        408, 429 -> true
        else -> this.code >= 500
    }
    is AppError.Unauthorized,
    is AppError.Serialization,
    is AppError.Unknown -> false
}
