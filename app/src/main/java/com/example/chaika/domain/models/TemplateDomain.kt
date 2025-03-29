package com.example.chaika.domain.models

/**
 * Доменная модель содержимого шаблона.
 *
 * @param productId Идентификатор продукта.
 * @param quantity Количество единиц продукта в шаблоне.
 */
data class TemplateContentDomain(
    val productId: Int,
    val quantity: Int
)

/**
 * Доменная модель шаблона.
 *
 * @param id Уникальный идентификатор шаблона.
 * @param templateName Название шаблона.
 * @param description Описание шаблона.
 * @param content Список содержимого шаблона (товаров и их количества).
 */
data class TemplateDomain(
    val id: Int,
    val templateName: String,
    val description: String,
    val content: List<TemplateContentDomain>
)
