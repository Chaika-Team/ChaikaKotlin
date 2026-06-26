package com.chaikasoft.app.diagnostics

import com.chaikasoft.app.domain.common.AppError

object DiagnosticPolicy {
    fun fromAppError(
        error: AppError,
        area: DiagnosticArea,
        operation: DiagnosticOperation
    ): DiagnosticEvent? = when (error) {
        is AppError.Unknown -> DiagnosticEvent(
            type = DiagnosticEventType.APP_ERROR_UNKNOWN,
            area = area,
            operation = operation,
            throwable = error.cause
        )
        is AppError.Serialization -> DiagnosticEvent(
            type = DiagnosticEventType.APP_ERROR_SERIALIZATION,
            area = area,
            operation = operation,
            throwable = error.cause
        )
        AppError.Network,
        AppError.Timeout,
        is AppError.Http,
        is AppError.Unauthorized -> null
    }
}
