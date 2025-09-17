package com.chaikasoft.app.data.room.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.chaikasoft.app.data.room.entities.CartOperation
import com.chaikasoft.app.data.room.entities.Conductor

/** Операция корзины + связанный проводник (может отсутствовать). */
data class CartOperationWithConductor(
    @Embedded val operation: CartOperation,
    @Relation(
        parentColumn = "conductor_id",
        entityColumn = "id"
    )
    val conductor: Conductor?    // LEFT JOIN-поведение: null, если записи нет
)
