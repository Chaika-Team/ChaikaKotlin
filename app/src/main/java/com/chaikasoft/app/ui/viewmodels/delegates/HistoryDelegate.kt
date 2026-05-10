package com.chaikasoft.app.ui.viewmodels.delegates

import android.util.Log
import androidx.annotation.StringRes
import com.chaikasoft.app.R
import com.chaikasoft.app.domain.models.trip.ConductorTripShiftDomain
import com.chaikasoft.app.domain.sealed.SendReportResult
import com.chaikasoft.app.domain.usecases.GetShiftHistoryUseCase
import com.chaikasoft.app.domain.usecases.SendShiftReportUseCase
import java.io.IOException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class HistoryDelegate(
    private val getShiftHistoryUseCase: GetShiftHistoryUseCase,
    private val sendShiftReportUseCase: SendShiftReportUseCase,
    private val scope: CoroutineScope
) {

    // ── Модели диалогов ──

    data class RetryConfirm(val uuid: String)
    data class RetryResult(@StringRes val messageRes: Int)

    // ── История ──

    private val _trips = MutableStateFlow<List<ConductorTripShiftDomain>>(emptyList())
    val trips: StateFlow<List<ConductorTripShiftDomain>> = _trips.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var observeJob: Job? = null

    // ── Подтверждение ретрая ──

    private val _retryConfirm = MutableStateFlow<RetryConfirm?>(null)
    val retryConfirm: StateFlow<RetryConfirm?> = _retryConfirm.asStateFlow()

    // ── Результат ретрая ──

    private val _retryResult = MutableStateFlow<RetryResult?>(null)
    val retryResult: StateFlow<RetryResult?> = _retryResult.asStateFlow()

    // ── Наблюдение за историей ──

    fun startObserving() {
        if (observeJob?.isActive == true) return

        observeJob = scope.launch {
            _isLoading.value = true
            try {
                getShiftHistoryUseCase()
                    .onStart { _isLoading.value = true }
                    .catch { e ->
                        Log.e(TAG, "Failed to load history", e)
                        _isLoading.value = false
                    }
                    .collect { historyList ->
                        _trips.value = historyList
                        _isLoading.value = false
                    }
            } catch (e: IOException) {
                Log.e(TAG, "Failed to observe history", e)
                _isLoading.value = false
            }
        }
    }

    fun stopObserving() {
        observeJob?.cancel()
        observeJob = null
    }

    // ── Ретрай: шаг 1 — запрос подтверждения ──

    fun requestRetrySend(uuid: String) {
        Log.d(TAG, "requestRetrySend: uuid=$uuid")
        _retryConfirm.value = RetryConfirm(uuid)
    }

    fun dismissRetryConfirm() {
        _retryConfirm.value = null
    }

    // ── Ретрай: шаг 2 — подтверждение и отправка ──

    fun confirmRetrySend() {
        val uuid = _retryConfirm.value?.uuid ?: return
        _retryConfirm.value = null

        scope.launch {
            Log.d(TAG, "confirmRetrySend: start, uuid=$uuid")
            try {
                val msg = when (sendShiftReportUseCase(uuid)) {
                    is SendReportResult.Success -> R.string.trip_finish_success
                    is SendReportResult.AlreadySent -> R.string.trip_finish_already_sent
                    is SendReportResult.MissingReport -> R.string.trip_finish_missing_report
                    is SendReportResult.TemporaryFailure -> R.string.trip_finish_temp_failure
                    is SendReportResult.PermanentFailure -> R.string.trip_finish_perm_failure
                }
                Log.d(TAG, "confirmRetrySend: result msgRes=$msg, uuid=$uuid")
                _retryResult.value = RetryResult(msg)
                // обновить список
                stopObserving()
                startObserving()
            } catch (e: IOException) {
                Log.e(TAG, "confirmRetrySend: error, uuid=$uuid", e)
                _retryResult.value = RetryResult(R.string.trip_finish_temp_failure)
            }
        }
    }

    // ── Ретрай: шаг 3 — закрыть результат ──

    fun dismissRetryResult() {
        _retryResult.value = null
    }

    private companion object {
        const val TAG = "HistoryDelegate"
    }
}
