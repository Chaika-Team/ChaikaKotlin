package com.chaikasoft.app.util

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldMatch
import java.time.ZoneId
import java.time.ZonedDateTime

class OperationTimeParserTest : FunSpec({

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности.
     *
     * Описание:
     *   - Проверяем все поддерживаемые ветки парсинга в toZoned:
     *     ZonedDateTime, OffsetDateTime, Instant, ISO_LOCAL_DATE_TIME.
     */
    test("toZoned parses supported date-time formats") {
        val zone = ZoneId.of("Europe/Moscow")

        "2026-03-09T12:00:00+03:00[Europe/Moscow]".toZoned(zone).toInstant().toString() shouldBe "2026-03-09T09:00:00Z"
        "2026-03-09T12:00:00+03:00".toZoned(ZoneId.of("UTC")).toInstant().toString() shouldBe "2026-03-09T09:00:00Z"
        "2026-03-09T09:00:00Z".toZoned(zone).toInstant().toString() shouldBe "2026-03-09T09:00:00Z"
        "2026-03-09T12:00:00".toZoned(zone).toInstant().toString() shouldBe "2026-03-09T09:00:00Z"
    }

    /**
     * Техника тест-дизайна: #2 Граничные значения.
     *
     * Описание:
     *   - Невалидная строка должна приводить к IllegalArgumentException.
     */
    test("toZoned throws IllegalArgumentException for invalid input") {
        val error = shouldThrow<IllegalArgumentException> {
            "not-a-date".toZoned()
        }

        error.message shouldContain "Unable to parse date-time string"
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности.
     *
     * Описание:
     *   - formatRuShort должен возвращать строку вида dd MMM HH:mm.
     *   - Проверяем структурный формат без привязки к конкретному названию месяца.
     */
    test("formatRuShort returns expected short pattern") {
        val value = ZonedDateTime.parse("2026-03-09T12:34:00+03:00[Europe/Moscow]").formatRuShort()

        value shouldMatch Regex("""\d{2}\s.{3,4}\s\d{2}:\d{2}""")
    }
})

