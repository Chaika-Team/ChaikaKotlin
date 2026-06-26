package com.chaikasoft.app.diagnostics

import io.sentry.Sentry
import io.sentry.SentryLevel
import javax.inject.Inject

class SentryErrorReporter @Inject constructor() : ErrorReporter {
    @Volatile
    private var context: DiagnosticContext? = null

    override fun recordNonFatal(event: DiagnosticEvent) {
        val safeThrowable = DiagnosticSanitizer.safeThrowable(event.throwable, event.type)
        val tags = event.safeTags()
        Sentry.withScope { scope ->
            context?.let { current ->
                scope.setTag(DiagnosticTag.APP_AREA.key, current.area.wireName)
                scope.setTag(DiagnosticTag.OPERATION.key, current.operation.wireName)
            }
            tags.forEach { (key, value) -> scope.setTag(key, value) }
            scope.level = event.severity.toSentryLevel()
            Sentry.captureException(safeThrowable)
        }
    }

    override fun setContext(context: DiagnosticContext) {
        this.context = context
    }

    override fun clearContext() {
        context = null
    }

    private fun DiagnosticSeverity.toSentryLevel(): SentryLevel = when (this) {
        DiagnosticSeverity.WARNING -> SentryLevel.WARNING
        DiagnosticSeverity.ERROR -> SentryLevel.ERROR
        DiagnosticSeverity.FATAL -> SentryLevel.FATAL
    }
}
