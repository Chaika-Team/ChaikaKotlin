package com.chaikasoft.app.data.datasource.repo

import android.util.Log
import com.chaikasoft.app.data.datasource.apiservice.ChaikaSoftApiService
import com.chaikasoft.app.domain.sealed.UploadResult
import java.io.IOException
import javax.inject.Inject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

private const val SEND_TAG = "SHIFT-REPORT"

class ChaikaSoftReportsRepository @Inject constructor(private val api: ChaikaSoftApiService) :
    ChaikaSoftReportsRepositoryInterface {

    override suspend fun uploadShiftReport(reportJson: String): UploadResult = try {
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
