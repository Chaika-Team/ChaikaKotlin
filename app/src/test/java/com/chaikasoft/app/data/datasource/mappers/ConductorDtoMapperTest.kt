package com.chaikasoft.app.data.datasource.mappers

import com.chaikasoft.app.data.datasource.dto.ConductorDto
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private const val DEFAULT_IMAGE =
    "https://i.pinimg.com/736x/5b/d3/e7/5bd3e779f192cb04cf35b859e0d50cbc.jpg"

class ConductorDtoMapperTest : FunSpec({

    /**
     * Техника тест-дизайна: #3 Таблица решений
     *
     * Описание:
     *   - Вход: DTO с заполненным image.
     *   - Ожидаемое поведение: nickname маппится в employeeID, image сохраняется как есть, id фиксируется в 0.
     *   - Цель: защитить основной контракт маппинга ConductorDto -> ConductorDomain.
     */
    test("maps nickname to employeeID and keeps explicit image") {
        val dto = ConductorDto(
            name = "Ivan",
            familyName = "Petrov",
            givenName = "Ivanovich",
            nickname = "EMP-001",
            image = "https://example.com/avatar.png",
        )

        val domain = dto.toDomain()

        domain.id shouldBe 0
        domain.name shouldBe "Ivan"
        domain.familyName shouldBe "Petrov"
        domain.givenName shouldBe "Ivanovich"
        domain.employeeID shouldBe "EMP-001"
        domain.image shouldBe "https://example.com/avatar.png"
    }

    /**
     * Техника тест-дизайна: #2 Граничные значения
     *
     * Описание:
     *   - Граница: image == null.
     *   - Ожидаемое поведение: подставляется дефолтная ссылка на изображение.
     *   - Цель: зафиксировать безопасный fallback для отсутствующего аватара.
     */
    test("uses fallback image when dto image is null") {
        val dto = ConductorDto(
            name = "Ivan",
            familyName = "Petrov",
            givenName = "Ivanovich",
            nickname = "EMP-001",
            image = null,
        )

        val domain = dto.toDomain()

        domain.image shouldBe DEFAULT_IMAGE
    }
})
