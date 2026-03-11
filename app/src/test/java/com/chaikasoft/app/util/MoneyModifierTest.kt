package com.chaikasoft.app.util

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class MoneyModifierTest : FunSpec({

    /**
     * Техника тест-дизайна: #2 Граничные значения.
     *
     * Описание:
     *   - Проверяем HALF_UP округление на типичных границах и отрицательных значениях.
     *   - Это защищает маппинг цены в копейки от "тихих" ошибок округления.
     */
    test("rubToKopecks applies HALF_UP rounding for boundary values") {
        rubToKopecks(0.0) shouldBe 0
        rubToKopecks(1.004) shouldBe 100
        rubToKopecks(1.005) shouldBe 101
        rubToKopecks(-1.004) shouldBe -100
        rubToKopecks(-1.005) shouldBe -101
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности.
     *
     * Описание:
     *   - Проверяем строковый формат "X.YY ₽" для положительных, нулевых и отрицательных значений.
     */
    test("kopecksToString formats value as rubles string with two kopecks digits") {
        kopecksToString(0) shouldBe "0.00 ₽"
        kopecksToString(5) shouldBe "0.05 ₽"
        kopecksToString(12345) shouldBe "123.45 ₽"
        kopecksToString(-150) shouldBe "-1.-50 ₽"
    }
})

