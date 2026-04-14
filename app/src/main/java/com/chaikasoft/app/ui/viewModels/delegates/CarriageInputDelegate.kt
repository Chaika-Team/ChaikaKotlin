package com.chaikasoft.app.ui.viewmodels.delegates

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class CarriageInputDelegate(scope: CoroutineScope) {

    private val _number = MutableStateFlow("")
    val number: StateFlow<String> = _number.asStateFlow()

    val isValid: StateFlow<Boolean> =
        _number
            .map { it.toIntOrNull()?.let { n -> n in 1..MAX } == true }
            .stateIn(scope, SharingStarted.WhileSubscribed(5_000), false)

    /** Возвращает валидный номер или null */
    val validatedNumber: Int?
        get() = _number.value.toIntOrNull()?.takeIf { it in 1..MAX }

    fun onNumberChanged(raw: String) {
        if (raw.isEmpty()) {
            _number.value = ""
            return
        }

        val digits = raw.filter(Char::isDigit)
        if (digits.isEmpty()) return

        val normalized = digits.trimStart('0')
        if (normalized.isEmpty()) {
            _number.value = ""
            return
        }
        if (normalized.length > 2) return

        val n = normalized.toIntOrNull() ?: return
        if (n !in 1..MAX) return

        _number.value = normalized
    }

    fun reset() {
        _number.value = ""
    }

    private companion object {
        const val MAX = 99
    }
}
