package com.chaikasoft.app.domain.sealed

sealed class SendReportResult {
    data object Success : SendReportResult()
    data object AlreadySent : SendReportResult()
    data object MissingReport : SendReportResult()            // нет JSON в БД
    data class TemporaryFailure(                              // можно ретраить
        val httpCode: Int? = null,
        val isNetwork: Boolean = false
    ) : SendReportResult()
    data class PermanentFailure(                              // бесполезно ретраить, траблы бека
        val httpCode: Int,
        val serverBody: String? = null
    ) : SendReportResult()
}
