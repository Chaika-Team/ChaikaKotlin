package com.example.chaika.data.dataSource.mappers

import com.example.chaika.data.dataSource.dto.TemplateDto
import com.example.chaika.data.dataSource.dto.TemplateListResponseDto
import com.example.chaika.domain.models.TemplateContentDomain
import com.example.chaika.domain.models.TemplateDomain

fun TemplateDto.toDomain(): TemplateDomain =
    TemplateDomain(
        id = this.id,
        templateName = this.templateName,
        description = this.description,
        content = this.content.map { dto ->
            TemplateContentDomain(
                productId = dto.productId,
                quantity = dto.quantity
            )
        }
    )

fun TemplateListResponseDto.toDomainList(): List<TemplateDomain> =
    this.templates.map { it.toDomain() }
