package com.chaikasoft.app.data.datasource.common

import com.chaikasoft.app.domain.common.AppError
import com.chaikasoft.app.domain.common.ErrorReporter
import com.chaikasoft.app.domain.common.RemoteResult
import com.google.gson.JsonParseException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException

@Suppress("TooGenericExceptionCaught")
suspend fun <T> remoteCall(
    errorReporter: ErrorReporter = ErrorReporter.NoOp,
    call: suspend () -> T
): RemoteResult<T> = try {
    RemoteResult.Success(call())
} catch (e: CancellationException) {
    throw e
} catch (e: HttpException) {
    val code = e.code()
    val body = runCatching { e.response()?.errorBody()?.string() }.getOrNull()

    val mapped = when (code) {
        401, 403 -> AppError.Unauthorized(code, e)
        else -> AppError.Http(code, body, e)
    }
    RemoteResult.Failure(mapped)
} catch (e: SocketTimeoutException) {
    RemoteResult.Failure(AppError.Timeout(e))
} catch (e: UnknownHostException) {
    RemoteResult.Failure(AppError.Network(e))
} catch (e: IOException) {
    RemoteResult.Failure(AppError.Network(e))
} catch (e: JsonParseException) {
    RemoteResult.Failure(AppError.Serialization(e))
} catch (e: Exception) {
    // Boundary fallback: keep unexpected mapper/client failures visible to diagnostics.
    errorReporter.recordNonFatal(e)
    RemoteResult.Failure(AppError.Unknown(e))
}
