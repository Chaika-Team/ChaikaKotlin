package com.chaikasoft.app.domain.common

sealed interface AppError {
    data object Network : AppError
    data object Timeout : AppError

    // хранит реальный код (401 или 403)
    data class Unauthorized(val code: Int) : AppError

    data class Http(val code: Int, val body: String? = null) : AppError
    data class Serialization(val cause: Exception) : AppError
    data class Unknown(val cause: Exception) : AppError
}
