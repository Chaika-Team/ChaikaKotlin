package com.example.chaika.data.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Сущность элемента корзины.
 *
 * @param id Уникальный идентификатор элемента корзины.
 * @param cartOperationId Идентификатор операции корзины, с которой связан элемент.
 * @param productId Идентификатор продукта.
 * @param impact Количество единиц продукта (может быть отрицательным для продаж).
 */
@Entity(
    tableName = "cart_items",
    foreignKeys = [
        ForeignKey(
            entity = CartOperation::class,
            parentColumns = ["id"],
            childColumns = ["cart_operation_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = ProductInfo::class,
            parentColumns = ["id"],
            childColumns = ["product_id"],
            onDelete = ForeignKey.CASCADE,
        ),

    ],
    indices = [
        Index(value = ["cart_operation_id"]),
        Index(value = ["product_id"]),
    ],
)
data class CartItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "cart_operation_id") val cartOperationId: Int,
    @ColumnInfo(name = "product_id") val productId: Int,
    @ColumnInfo(name = "impact") val impact: Int,
)
