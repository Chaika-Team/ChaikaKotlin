package com.chaikasoft.app.domain.models.trip

/** Статус смены проводника. */
enum class TripShiftStatusDomain(val code: Int) {
    ACTIVE(0),
    FINISHED(1),
    SENT(2);

    companion object {
        fun withCode(code: Int): TripShiftStatusDomain = entries.firstOrNull { it.code == code }
            ?: throw IllegalArgumentException("Unknown TripShiftStatusDomain code=$code")
    }
}
