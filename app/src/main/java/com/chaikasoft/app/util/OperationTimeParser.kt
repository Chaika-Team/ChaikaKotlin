package com.chaikasoft.app.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Форматирует ZonedDateTime как `dd MMM HH:mm` с текущей локалью приложения. */
fun ZonedDateTime.formatRuShort(): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMM HH:mm", Locale.getDefault())
    return this.format(formatter)
}

/** Безопасно парсит ISO-строку в ZonedDateTime с учётом переданной зоны (по умолчанию — системная) */
fun String.toZoned(zone: ZoneId = ZoneId.systemDefault()): ZonedDateTime =
    runCatching { ZonedDateTime.parse(this) }.getOrElse {
        runCatching { OffsetDateTime.parse(this).atZoneSameInstant(zone) }.getOrElse {
            runCatching { Instant.parse(this).atZone(zone) }.getOrElse {
                runCatching {
                    LocalDateTime.parse(this, DateTimeFormatter.ISO_LOCAL_DATE_TIME).atZone(zone)
                }.getOrElse {
                    throw IllegalArgumentException("Unable to parse date-time string: $this", it)
                }
            }
        }
    }
