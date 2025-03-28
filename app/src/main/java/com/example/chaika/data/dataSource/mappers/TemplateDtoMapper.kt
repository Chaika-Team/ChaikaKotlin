package com.example.chaika.data.dataSource.mappers

import com.example.chaika.data.dataSource.dto.TemplateDto
import com.example.chaika.domain.models.TemplateContentDomain
import com.example.chaika.domain.models.TemplateDomain

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

