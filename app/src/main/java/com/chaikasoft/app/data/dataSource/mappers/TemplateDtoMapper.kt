package com.chaikasoft.app.data.dataSource.mappers

import com.chaikasoft.app.data.dataSource.dto.TemplateDto
import com.chaikasoft.app.domain.models.TemplateContentDomain
import com.chaikasoft.app.domain.models.TemplateDomain

fun TemplateDto.toDomain(): TemplateDomain {
    return TemplateDomain(
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
}

fun List<TemplateDto>.toDomainList(): List<TemplateDomain> =
    map { it.toDomain() }

