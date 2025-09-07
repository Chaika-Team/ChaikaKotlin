package com.chaikasoft.app.domain.sealed

sealed class LogoutResult {
    data object Success : LogoutResult()
    data object ActiveShiftExists : LogoutResult()
    data class Failure(val reason: String) : LogoutResult()
}
