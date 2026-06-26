package com.chaikasoft.app.diagnostics

import javax.inject.Inject

class NoOpErrorReporter @Inject constructor() : ErrorReporter {
    override fun recordNonFatal(event: DiagnosticEvent) = Unit
    override fun setContext(context: DiagnosticContext) = Unit
    override fun clearContext() = Unit
}
