package com.example.chaika.data.room.entities

import androidx.room.ColumnInfo
import androidx.room.DatabaseView

/**
 * Представление для текущего количества товаров у проводника.
 *
 * @param productId Идентификатор продукта.
 * @param currentQuantity Текущее количество единиц продукта.
 */
@DatabaseView(
    viewName = "package_items",
    value = """
        SELECT product_id, SUM(impact) as current_quantity 
        FROM cart_items 
        GROUP BY product_id
    """
)
data class PackageItemView(
    @ColumnInfo(name = "product_id") val productId: Int,
    @ColumnInfo(name = "current_quantity") val currentQuantity: Int
)
