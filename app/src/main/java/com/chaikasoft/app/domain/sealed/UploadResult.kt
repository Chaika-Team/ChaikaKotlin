package com.chaikasoft.app.domain.sealed

sealed class UploadResult {
    data object Ok : UploadResult()
    data class HttpError(val code: Int, val body: String?) : UploadResult()
    data class NetworkError(val throwable: Throwable) : UploadResult()
}
