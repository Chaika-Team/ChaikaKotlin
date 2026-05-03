package com.chaikasoft.app.util

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ImageUrlUtilsTest : FunSpec({

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Описание:
     *   - Вход: валидные remote URL со схемами http и https, включая пробелы по краям.
     *   - Ожидаемое поведение: функция возвращает trim-версию исходного URL.
     *   - Цель: зафиксировать нормализацию допустимых remote image URL.
     */
    test("returns trimmed remote http and https urls") {
        " http://example.test/image.jpg ".normalizedRemoteImageUrlOrNull() shouldBe
            "http://example.test/image.jpg"
        "https://example.test/image.jpg".normalizedRemoteImageUrlOrNull() shouldBe
            "https://example.test/image.jpg"
    }

    /**
     * Техника тест-дизайна: #2 Граничные значения
     *
     * Описание:
     *   - Вход: remote URL со схемой в верхнем регистре.
     *   - Ожидаемое поведение: схема распознаётся без учёта регистра.
     *   - Цель: защитить URL-валидацию от регистрозависимых ошибок.
     */
    test("accepts uppercase remote url schemes") {
        "HTTPS://example.test/image.jpg".normalizedRemoteImageUrlOrNull() shouldBe
            "HTTPS://example.test/image.jpg"
    }

    /**
     * Техника тест-дизайна: #3 Таблица решений
     *
     * Описание:
     *   - Вход: пустая строка, blank, локальный путь, ftp URL и произвольный текст.
     *   - Ожидаемое поведение: функция возвращает null для всех не-remote значений.
     *   - Цель: не допустить передачи placeholder, локальных путей и мусорных строк в загрузчик изображений.
     */
    test("returns null for non-remote image references") {
        "".normalizedRemoteImageUrlOrNull() shouldBe null
        "   ".normalizedRemoteImageUrlOrNull() shouldBe null
        "files/products/Tea.jpg".normalizedRemoteImageUrlOrNull() shouldBe null
        "ftp://example.test/image.jpg".normalizedRemoteImageUrlOrNull() shouldBe null
        "not-a-url".normalizedRemoteImageUrlOrNull() shouldBe null
    }
})
