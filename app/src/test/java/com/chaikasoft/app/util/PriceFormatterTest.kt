package com.chaikasoft.app.util

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class PriceFormatterTest : FunSpec({

    /**
     * Техника тест-дизайна: #7 Таблица решений.
     *
     * Описание:
     *   - Проверяем форматирование общей стоимости с учетом количества.
     *   - Фиксируем текущее поведение для нулевых и отрицательных quantity.
     */
    test("formatPrice formats total price for various quantity values") {
        formatPrice(priceKopecks = 1234, quantity = 2) shouldBe "24.68 ₽"
        formatPrice(priceKopecks = 1234, quantity = 0) shouldBe "0.00 ₽"
        formatPrice(priceKopecks = 150, quantity = -1) shouldBe "-1.-50 ₽"
    }

    /**
     * Техника тест-дизайна: #2 Граничные значения.
     *
     * Описание:
     *   - Проверяем большой total: реализация использует Long и не должна терять корректность.
     */
    test("formatPrice handles large totals without Int overflow") {
        formatPrice(priceKopecks = 2_000_000_000, quantity = 2) shouldBe "40000000.00 ₽"
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности.
     *
     * Описание:
     *   - Проверяем форматирование цены без множителя количества.
     */
    test("formatPriceOnly formats standalone kopecks value") {
        formatPriceOnly(0) shouldBe "0.00 ₽"
        formatPriceOnly(5) shouldBe "0.05 ₽"
        formatPriceOnly(12345) shouldBe "123.45 ₽"
        formatPriceOnly(-150) shouldBe "-1.-50 ₽"
    }
})

