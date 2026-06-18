package com.chaikasoft.app.data.diagnostics

import android.util.Log
import com.chaikasoft.app.domain.common.ErrorReporter
import javax.inject.Inject

class LogcatErrorReporter @Inject constructor() : ErrorReporter {
    override fun recordNonFatal(error: Throwable) {
        Log.e(TAG, "Non-fatal unexpected error", error)
    }

    private companion object {
        const val TAG = "ErrorReporter"
    }
}
