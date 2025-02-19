package com.example.chaika.data.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Сущность информации о продукте.
 *
 * @param id Уникальный идентификатор продукта.
 * @param name Название продукта.
 * @param description Описание продукта.
 * @param image Ссылка или путь к изображению продукта.
 * @param price Цена продукта.
 */
@Entity(tableName = "product_info")
data class ProductInfo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "image") val image: String,
    @ColumnInfo(name = "price") val price: Double
)
