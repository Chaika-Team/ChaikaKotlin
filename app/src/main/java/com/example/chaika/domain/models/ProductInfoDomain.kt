package com.example.chaika.domain.models

/**
 * Доменная модель информации о продукте.
 *
 * @param id Уникальный идентификатор продукта.
 * @param name Название продукта.
 * @param description Описание продукта.
 * @param image Ссылка или путь к изображению продукта.
 * @param price Цена продукта.
 */
data class ProductInfoDomain(
    val id: Int,
    val name: String,
    val description: String,
    val image: String,
    val price: Double
)
