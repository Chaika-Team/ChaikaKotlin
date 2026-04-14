package com.chaikasoft.app.data.datasource.mappers

import com.chaikasoft.app.data.datasource.dto.TemplateDto
import com.chaikasoft.app.domain.models.TemplateContentDomain
import com.chaikasoft.app.domain.models.TemplateDomain

fun TemplateDto.toDomain(): TemplateDomain = TemplateDomain(
    id = this.id,
    templateName = this.templateName,
    description = this.description,
    content = this.content.map { contentDto ->
        // Преобразуем каждый элемент content
        TemplateContentDomain(
            productId = contentDto.productId,
            quantity = contentDto.quantity
        )
    }
)

fun List<TemplateDto>.toDomainList(): List<TemplateDomain> = map { it.toDomain() }
