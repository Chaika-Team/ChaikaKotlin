package com.example.chaika.data.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Сущность операции с корзиной.
 *
 * @param id Уникальный идентификатор операции.
 * @param operationType Тип операции (0 - ADD, 1 - SOLD_CASH, 2 - SOLD_CART, 3 - REPLENISH).
 * @param operationTime Время выполнения операции в формате ISO 8601.
 * @param conductorId Идентификатор проводника, связанного с операцией.
 */
@Entity(
    tableName = "cart_operations",
    foreignKeys = [
        ForeignKey(
            entity = Conductor::class,
            parentColumns = ["id"],
            childColumns = ["conductor_id"],
        ),
    ],
    indices = [
        Index(value = ["conductor_id"]),
        Index(value = ["operation_time", "id"])
    ],
)
data class CartOperation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "operation_type") val operationType: Int,
    @ColumnInfo(name = "operation_time") val operationTime: String,
    @ColumnInfo(name = "conductor_id") val conductorId: Int
)
