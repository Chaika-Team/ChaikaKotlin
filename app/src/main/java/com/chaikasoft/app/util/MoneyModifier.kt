package com.chaikasoft.app.util

import java.math.BigDecimal
import java.math.RoundingMode

/** Рубли (Double) → копейки (Int) с безопасным округлением HALF_UP */
fun rubToKopecks(rub: Double): Int = BigDecimal.valueOf(rub)
    .multiply(BigDecimal.valueOf(100))
    .setScale(0, RoundingMode.HALF_UP)
    .toInt()

/** Копейки (Int) → строка "X.YY ₽" (если где-то вне PriceFormatter понадобится) */
fun kopecksToString(kopecks: Int): String {
    val rub = kopecks / 100
    val kop = kopecks % 100
    return "%d.%02d ₽".format(rub, kop)
}
