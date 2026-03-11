package com.chaikasoft.app.util

import com.chaikasoft.app.domain.models.trip.StationDomain
import com.chaikasoft.app.domain.models.trip.TripDomain
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.TimeZone

class TripTimeParserTest : FunSpec({

    val originalTz = TimeZone.getDefault()

    beforeSpec {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    afterSpec {
        TimeZone.setDefault(originalTz)
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности.
     *
     * Описание:
     *   - Валидный ISO Instant должен корректно форматироваться в dd.MM и HH:mm.
     */
    test("getDayMonth and getTime parse valid instant input") {
        val value = "2026-03-09T12:34:56Z"

        getDayMonth(value) shouldBe "09.03"
        getTime(value) shouldBe "12:34"
    }

    /**
     * Техника тест-дизайна: #5 Error Guessing.
     *
     * Описание:
     *   - Невалидная строка должна обрабатываться fallback-значением N/A.
     */
    test("getDayMonth and getTime return N/A for invalid input") {
        getDayMonth("bad") shouldBe "N/A"
        getTime("bad") shouldBe "N/A"
    }

    /**
     * Техника тест-дизайна: #7 Таблица решений.
     *
     * Описание:
     *   - parseDuration поддерживает ISO-8601 и альтернативный h/m/s формат.
     */
    test("parseDuration supports ISO and alternative formats") {
        parseDuration("PT2H35M") shouldBe (2 to 35)
        parseDuration("3h29m0s") shouldBe (3 to 29)
    }

    /**
     * Техника тест-дизайна: #2 Граничные значения.
     *
     * Описание:
     *   - Невалидная длительность должна давать безопасный результат 0 to 0.
     */
    test("parseDuration returns zero pair for invalid duration") {
        parseDuration("invalid-duration") shouldBe (0 to 0)
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности.
     *
     * Описание:
     *   - parseTripDetails корректно объединяет результаты getDayMonth/getTime/parseDuration.
     */
    test("parseTripDetails maps all trip fields into TripDetails") {
        val trip = TripDomain(
            uuid = "uuid",
            trainNumber = "100A",
            departure = "2026-03-09T10:00:00Z",
            arrival = "2026-03-09T12:35:00Z",
            duration = "PT2H35M",
            from = StationDomain("MSK", "Moscow", "Moscow"),
            to = StationDomain("TVE", "Tver", "Tver"),
        )

        trip.parseTripDetails() shouldBe TripDetails(
            departureDayMonth = "09.03",
            arrivalDayMonth = "09.03",
            departureTime = "10:00",
            arrivalTime = "12:35",
            durationHours = 2,
            durationMinutes = 35,
        )
    }
})

