package com.example.chaika.data.dataSource.dto

import com.google.gson.annotations.SerializedName

data class TemplateContentDto(
    @SerializedName("product_id")
    val productId: Int,
    val quantity: Int
)

// DTO для шаблона, получаемого через поиск (без контента или с пустым content)
data class TemplateDto(
    val id: Int,
    @SerializedName("templateName")
    val templateName: String,
    val description: String,
    val content: List<TemplateContentDto> = emptyList() // По умолчанию пустой
)

// DTO для списка шаблонов
data class TemplateListResponseDto(
    @SerializedName("templates")
    val templates: List<TemplateDto>
)

// DTO для детальной информации о шаблоне (с заполненным content)
data class TemplateDetailResponseDto(
    @SerializedName("Template")
    val template: TemplateDto
)