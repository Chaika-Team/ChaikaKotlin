package com.chaikasoft.app.data.datasource.mappers

import com.chaikasoft.app.data.datasource.dto.ProductInfoDto
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ProductInfoDtoMapperTest : FunSpec({

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Описание:
     *   - Вход: DTO продукта с заполненным image и дробной ценой.
     *   - Ожидаемое поведение: поля маппятся без подмены image, цена переводится в копейки.
     *   - Цель: зафиксировать, что data-слой сохраняет URL сервиса и не подставляет placeholder.
     */
    test("maps dto to domain and keeps explicit image") {
        val dto = ProductInfoDto(
            id = 7,
            name = "Tea",
            description = "Black tea",
            image = "https://example.com/from-api.png",
            price = 12.345,
        )

        val domain = dto.toDomain()

        domain.id shouldBe 7
        domain.name shouldBe "Tea"
        domain.description shouldBe "Black tea"
        domain.image shouldBe "https://example.com/from-api.png"
        domain.price shouldBe 1235
    }

    /**
     * Техника тест-дизайна: #2 Граничные значения
     *
     * Описание:
     *   - Вход: DTO продукта с image == null.
     *   - Ожидаемое поведение: image в доменной модели становится пустой строкой.
     *   - Цель: защитить контракт отсутствующего изображения без внешнего placeholder URL.
     */
    test("maps missing dto image to empty string") {
        val dto = ProductInfoDto(
            id = 7,
            name = "Tea",
            description = "Black tea",
            image = null,
            price = 12.345,
        )

        val domain = dto.toDomain()

        domain.image shouldBe ""
    }
})
