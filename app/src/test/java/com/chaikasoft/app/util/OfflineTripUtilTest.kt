package com.chaikasoft.app.util

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import java.time.LocalDateTime
import java.time.ZoneId

class OfflineTripUtilTest : FunSpec({

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности.
     *
     * Описание:
     *   - localToRfc3339Utc должен корректно переводить локальное время в UTC с суффиксом Z.
     */
    test("localToRfc3339Utc converts local time to UTC RFC3339 string") {
        val local = LocalDateTime.of(2026, 3, 9, 15, 30, 45)
        val zone = ZoneId.of("Europe/Moscow") // UTC+3

        localToRfc3339Utc(local, zone) shouldBe "2026-03-09T12:30:45Z"
    }

    /**
     * Техника тест-дизайна: #2 Граничные значения.
     *
     * Описание:
     *   - durationHms не должен возвращать отрицательную длительность.
     *   - При arrival < departure результат должен быть "0h0m0s".
     */
    test("durationHms calculates duration and clamps negative result to zero") {
        val zone = ZoneId.of("UTC")
        val dep = LocalDateTime.of(2026, 3, 9, 10, 0, 0)
        val arr = LocalDateTime.of(2026, 3, 9, 13, 5, 7)

        durationHms(dep, arr, zone) shouldBe "3h5m7s"
        durationHms(arr, dep, zone) shouldBe "0h0m0s"
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности.
     *
     * Описание:
     *   - normalizeForDisplay должен trim-ить и схлопывать множественные пробелы.
     */
    test("normalizeForDisplay trims and collapses whitespace") {
        normalizeForDisplay("  Поезд   123   ") shouldBe "Поезд 123"
        normalizeForDisplay("\n\t  A   B \t C  ") shouldBe "A B C"
    }

    /**
     * Техника тест-дизайна: #5 Error Guessing.
     *
     * Описание:
     *   - UUID для локальной поездки должен иметь префикс local- и быть уникальным между вызовами.
     */
    test("generateLocalUuid adds local prefix and produces unique values") {
        val first = generateLocalUuid()
        val second = generateLocalUuid()

        first.shouldStartWith("local-")
        second.shouldStartWith("local-")
        first shouldBe first // sanity
        (first == second) shouldBe false
    }
})

