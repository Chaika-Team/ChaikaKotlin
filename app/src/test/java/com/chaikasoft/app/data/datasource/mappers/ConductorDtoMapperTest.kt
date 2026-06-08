package com.chaikasoft.app.data.datasource.mappers

import com.chaikasoft.app.data.datasource.dto.ConductorDto
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ConductorDtoMapperTest : FunSpec({

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Описание:
     *   - Вход: DTO проводника с заполненным picture и preferredUsername.
     *   - Ожидаемое поведение: OIDC claims маппятся в поля ConductorDomain по смыслу.
     *   - Цель: зафиксировать основной контракт ConductorDto -> ConductorDomain без подмены изображения.
     */
    test("maps oidc claims to conductor domain") {
        val dto = ConductorDto(
            firstName = "Ivan",
            familyName = "Petrov",
            middleName = "Ivanovich",
            preferredUsername = "EMP-001",
            picture = "https://example.com/avatar.png",
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
     *   - Вход: DTO проводника с image == null.
     *   - Ожидаемое поведение: image в доменной модели становится пустой строкой.
     *   - Цель: защитить fallback-контракт data-слоя без записи placeholder URL.
     */
    test("maps missing dto image to empty string") {
        val dto = ConductorDto(
            firstName = "Ivan",
            familyName = "Petrov",
            middleName = "Ivanovich",
            preferredUsername = "EMP-001",
            picture = null,
        )

        val domain = dto.toDomain()

        domain.image shouldBe ""
    }
})
