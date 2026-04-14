package com.chaikasoft.app.data.datasource.mappers

import com.chaikasoft.app.data.datasource.dto.TemplateContentDto
import com.chaikasoft.app.data.datasource.dto.TemplateDto
import com.chaikasoft.app.domain.models.TemplateContentDomain
import com.chaikasoft.app.domain.models.TemplateDomain
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TemplateDtoMapperTest : FunSpec({

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Описание:
     *   - Эквивалентный класс: шаблон с непустым content.
     *   - Ожидаемое поведение: каждый элемент content маппится без потерь.
     *   - Цель: зафиксировать корректное преобразование вложенной структуры.
     */
    test("maps template dto with content to domain") {
        val dto = TemplateDto(
            id = 11,
            templateName = "Breakfast",
            description = "Morning set",
            content = listOf(
                TemplateContentDto(productId = 1, quantity = 2),
                TemplateContentDto(productId = 2, quantity = 3),
            ),
        )

        val domain = dto.toDomain()

        domain shouldBe TemplateDomain(
            id = 11,
            templateName = "Breakfast",
            description = "Morning set",
            content = listOf(
                TemplateContentDomain(productId = 1, quantity = 2),
                TemplateContentDomain(productId = 2, quantity = 3),
            ),
        )
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Описание:
     *   - Вход: список из нескольких TemplateDto.
     *   - Ожидаемое поведение: toDomainList маппит все элементы в исходном порядке.
     *   - Цель: защитить контракт пакетного маппинга списков шаблонов.
     */
    test("toDomainList maps all items preserving order") {
        val dtos = listOf(
            TemplateDto(id = 1, templateName = "A", description = "d1", content = emptyList()),
            TemplateDto(id = 2, templateName = "B", description = "d2", content = emptyList()),
        )

        val domains = dtos.toDomainList()

        domains shouldBe listOf(
            TemplateDomain(id = 1, templateName = "A", description = "d1", content = emptyList()),
            TemplateDomain(id = 2, templateName = "B", description = "d2", content = emptyList()),
        )
    }
})
