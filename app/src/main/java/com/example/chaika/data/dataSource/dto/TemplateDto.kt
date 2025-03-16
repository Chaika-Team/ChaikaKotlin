package com.example.chaika.data.dataSource.dto

import com.google.gson.annotations.SerializedName

data class TemplateContentDto(
    @SerializedName("product_id") val productId: Int,
    val quantity: Int
)

data class TemplateDto(
    val id: Int,
    @SerializedName("template_name") val templateName: String,
    val description: String,
    val content: List<TemplateContentDto>
)

data class TemplateListResponseDto(
    @SerializedName("Templates") val templates: List<TemplateDto>
)
