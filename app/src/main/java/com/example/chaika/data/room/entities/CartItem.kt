package com.example.chaika.data.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "cart_items",
    foreignKeys = [
        ForeignKey(
            entity = CartOperation::class,
            parentColumns = ["id"],
            childColumns = ["cart_operation_id"]
        ),
        ForeignKey(
            entity = ProductInfo::class,
            parentColumns = ["id"],
            childColumns = ["product_id"]
        )
    ]
)
data class CartItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "cart_operation_id") val cartOperationId: Int, // Связываем с CartOperation
    @ColumnInfo(name = "product_id") val productId: Int,
    @ColumnInfo(name = "impact") val impact: Int // количество, может быть отрицательным
)
