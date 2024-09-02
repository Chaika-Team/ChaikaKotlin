package com.example.chaika.data.room.mappers

import com.example.chaika.data.room.entities.CartOperation as CartOperationEntity
import com.example.chaika.domain.models.CartOperation as CartOperationDomain
import com.example.chaika.domain.models.OperationType
import java.text.SimpleDateFormat
import java.util.Locale

// Преобразование Int в OperationType с использованием ordinal
fun Int.toOperationType(): OperationType {
    return OperationType.entries[this]
}

// Преобразование OperationType в Int с использованием ordinal
fun OperationType.toInt(): Int {
    return this.ordinal
}

// Утилита для получения текущего времени в формате ISO 8601
fun getCurrentTime(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    return dateFormat.format(System.currentTimeMillis())
}

fun CartOperationEntity.toDomain(): CartOperationDomain {
    return CartOperationDomain(
        operationType = this.operationType.toOperationType(),
        conductorId = this.conductorId
    )
}

fun CartOperationDomain.toEntity(): CartOperationEntity {
    return CartOperationEntity(
        id = 0, // Система автоматически назначит ID
        operationType = this.operationType.toInt(), // Используем ordinal для преобразования в Int
        operationTime = getCurrentTime(), // Установка текущих даты и времени
        conductorId = this.conductorId
    )
}
