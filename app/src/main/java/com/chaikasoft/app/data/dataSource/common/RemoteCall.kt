package com.chaikasoft.app.data.dataSource.common

import com.chaikasoft.app.domain.common.AppError
import com.chaikasoft.app.domain.common.RemoteResult
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

suspend inline fun <T> remoteCall(
    crossinline call: suspend () -> T
): RemoteResult<T> {
    return try {
        RemoteResult.Success(call())
    } catch (e: CancellationException) {
        throw e
    } catch (e: HttpException) {
        val code = e.code()
        val body = runCatching { e.response()?.errorBody()?.string() }.getOrNull()

        val mapped = when (code) {
            401, 403 -> AppError.Unauthorized(code)
            else -> AppError.Http(code, body)
        }
        RemoteResult.Failure(mapped)
    } catch (e: SocketTimeoutException) {
        RemoteResult.Failure(AppError.Timeout)
    } catch (e: UnknownHostException) {
        RemoteResult.Failure(AppError.Network)
    } catch (e: IOException) {
        RemoteResult.Failure(AppError.Network)
    } catch (e: com.google.gson.JsonParseException) {
        RemoteResult.Failure(AppError.Serialization(e))
    } catch (e: Exception) {
        // не Throwable!
        RemoteResult.Failure(AppError.Unknown(e))
    }
}
