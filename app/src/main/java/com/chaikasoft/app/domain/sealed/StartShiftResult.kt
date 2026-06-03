package com.chaikasoft.app.domain.sealed

sealed interface StartShiftResult {
    data object Started : StartShiftResult
    data object ActiveShiftAlreadyExists : StartShiftResult
    data object TripAlreadyRegistered : StartShiftResult
}
