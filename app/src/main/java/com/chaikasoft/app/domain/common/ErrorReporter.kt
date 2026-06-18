package com.chaikasoft.app.domain.common

fun interface ErrorReporter {
    fun recordNonFatal(error: Throwable)

    companion object {
        val NoOp = ErrorReporter { }
    }
}
