package com.chaikasoft.app.data.dataSource.repo

import android.util.Log
import com.chaikasoft.app.data.dataSource.apiService.ChaikaSoftApiService
import com.chaikasoft.app.domain.sealed.UploadResult
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import javax.inject.Inject

private const val SEND_TAG = "SHIFT-REPORT"

class ChaikaSoftReportsRepository @Inject constructor(
    private val api: ChaikaSoftApiService
) : ChaikaSoftReportsRepositoryInterface {

    override suspend fun uploadShiftReport(reportJson: String): UploadResult {
        return try {
            Log.d(SEND_TAG, "Repo: sendShiftReport POST, jsonLen=${reportJson.length}")
            val body = reportJson.toRequestBody("application/json; charset=utf-8".toMediaType())
            api.sendShiftReport(body)
            Log.d(SEND_TAG, "Repo: HTTP 2xx OK")
            UploadResult.Ok
        } catch (e: retrofit2.HttpException) {
            val code = e.code()
            val err = e.response()?.errorBody()?.string()
            Log.w(SEND_TAG, "Repo: HTTP $code errSnippet=${err?.take(256)}", e)
            UploadResult.HttpError(code = code, body = err)
        } catch (e: IOException) {
            Log.w(SEND_TAG, "Repo: IOException ${e.message}", e)
            UploadResult.NetworkError(e)
        } catch (e: Exception) {
            Log.e(SEND_TAG, "Repo: Unexpected ${e.javaClass.simpleName}: ${e.message}", e)
            UploadResult.NetworkError(e)
        }
    }
}
