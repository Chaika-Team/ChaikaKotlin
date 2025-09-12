package com.chaikasoft.app.util

import com.chaikasoft.app.R
import java.time.*

fun localToRfc3339(
    local: LocalDateTime,
    zone: ZoneId = ZoneId.systemDefault()
): String = local.atZone(zone).toOffsetDateTime().toString()

fun isoDuration(
    depLocal: LocalDateTime,
    arrLocal: LocalDateTime,
    zone: ZoneId = ZoneId.systemDefault()
): String {
    val dep = depLocal.atZone(zone)
    val arr = arrLocal.atZone(zone)
    val d = Duration.between(dep, arr)
    val h = d.toHours()
    val m = d.minusHours(h).toMinutes()
    return "PT${h}H${m}M"
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