package com.chaikasoft.app.util

import java.time.*
import java.time.temporal.ChronoUnit

/**
 * Локальное время → RFC 3339 UTC с секундами и суффиксом Z.
 * Пример: 2025-04-29T18:40:00Z
 */
fun localToRfc3339Utc(
    local: LocalDateTime,
    zone: ZoneId = ZoneId.systemDefault()
): String =
    local
        .atZone(zone)                 // трактуем локальное как время в заданной зоне
        .toInstant()                  // переводим в UTC
        .truncatedTo(ChronoUnit.SECONDS)
        .toString()                   // ISO_INSTANT → ...Z


/**
 * Продолжительность между отправлением и прибытием в формате <H>h<M>m<S>s,
 * как у сервера (например, "3h29m0s"), а не ISO-8601 PT..
 */
fun durationHms(
    depLocal: LocalDateTime,
    arrLocal: LocalDateTime,
    zone: ZoneId = ZoneId.systemDefault()
): String {
    val dep = depLocal.atZone(zone)
    val arr = arrLocal.atZone(zone)
    val seconds = Duration.between(dep, arr).seconds.coerceAtLeast(0)
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return "${h}h${m}m${s}s"
}

fun generateLocalUuid(): String = "local-${java.util.UUID.randomUUID()}"

/**
 * Нормализация для отображения пользователю:
 * - trim
 * - схлопывание пробелов
 * (без lowercase — сохраняем регистр)
 */
fun normalizeForDisplay(s: String): String =
    s.trim()
        .replace(Regex("\\s+"), " ")