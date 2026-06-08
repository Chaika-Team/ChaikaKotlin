package com.chaikasoft.app.domain.models

/**
 * Доменная модель содержимого шаблона.
 *
 * @param productId Идентификатор продукта.
 * @param quantity Количество единиц продукта в шаблоне.
 */
data class TemplateContentDomain(val productId: Int, val quantity: Int)

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

/**
 * Доменная модель детальной информации о шаблоне с разрешёнными товарами.
 *
 * @param template Сырой шаблон, полученный из сервиса шаблонов.
 * @param items Список строк шаблона, дополненных информацией о товарах из локальной базы данных.
 */
data class ResolvedTemplateDetailDomain(
    val template: TemplateDomain,
    val items: List<ResolvedTemplateItemDomain>
)

/**
 * Доменная модель строки шаблона с информацией о товаре из локальной базы данных.
 *
 * @param productId Идентификатор продукта из содержимого шаблона.
 * @param quantity Количество единиц продукта в шаблоне.
 * @param product Информация о продукте из локальной базы данных, если товар найден.
 */
data class ResolvedTemplateItemDomain(
    val productId: Int,
    val quantity: Int,
    val product: ProductInfoDomain?
)
