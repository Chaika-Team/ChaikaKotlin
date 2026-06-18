package com.chaikasoft.app.domain.common

import java.io.IOException
import java.net.SocketTimeoutException
import retrofit2.HttpException

sealed interface AppError {
    val cause: Throwable?
        get() = null

    data class Network(override val cause: IOException? = null) : AppError
    data class Timeout(override val cause: SocketTimeoutException? = null) : AppError

    data class Unauthorized(val code: Int, override val cause: HttpException? = null) : AppError

    data class Http(
        val code: Int,
        val body: String? = null,
        override val cause: HttpException? = null
    ) : AppError

    data class Serialization(override val cause: Exception) : AppError
    data class Unknown(override val cause: Exception) : AppError
}
