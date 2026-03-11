package com.chaikasoft.app.data.dataSource.mappers

import com.chaikasoft.app.data.dataSource.dto.ProductInfoDto
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private const val DEFAULT_IMAGE =
    "https://i.pinimg.com/736x/5b/d3/e7/5bd3e779f192cb04cf35b859e0d50cbc.jpg"

class ProductInfoDtoMapperTest : FunSpec({

    /**
     * Техника тест-дизайна: #2 Граничные значения
     *
     * Описание:
     *   - Вход: цена с дробной частью, требующей округления HALF_UP при переводе в копейки.
     *   - Ожидаемое поведение: корректная конвертация price и подстановка фиксированного image.
     *   - Цель: защитить от регрессий в расчетах денежных значений и контракте отображения картинки.
     */
    test("maps dto to domain and converts rubles to kopecks with HALF_UP") {
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
        domain.image shouldBe DEFAULT_IMAGE
        domain.price shouldBe 1235
    }
})
