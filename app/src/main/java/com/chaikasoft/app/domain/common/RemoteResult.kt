package com.chaikasoft.app.domain.common

sealed interface RemoteResult<out T> {
    data class Success<T>(val data: T) : RemoteResult<T>
    data class Failure(val error: AppError) : RemoteResult<Nothing>
}
