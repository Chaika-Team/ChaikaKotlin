package com.example.chaika.data.room.mappers

import com.example.chaika.data.room.entities.CartOperation
import com.example.chaika.domain.models.CartOperationDomain
import com.example.chaika.domain.models.OperationTypeDomain
import java.text.SimpleDateFormat
import java.util.Locale

// Преобразование Int в OperationType с использованием ordinal
fun Int.toOperationType(): OperationTypeDomain {
    return OperationTypeDomain.entries.getOrNull(this)
        ?: throw IllegalArgumentException("Unknown OperationType for index $this")
}

// Преобразование OperationType в Int с использованием ordinal
fun OperationTypeDomain.toInt(): Int {
    return this.ordinal
}

// Утилита для получения текущего времени в формате ISO 8601
fun getCurrentTime(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    return dateFormat.format(System.currentTimeMillis())
}

fun CartOperation.toDomain(): CartOperationDomain {
    return CartOperationDomain(
        operationTypeDomain = this.operationType.toOperationType(),
        conductorId = this.conductorId
    )
}

fun CartOperationDomain.toEntity(): CartOperation {
    return CartOperation(
        id = 0, // Система автоматически назначит ID
        operationType = this.operationTypeDomain.toInt(), // Используем ordinal для преобразования в Int
        operationTime = getCurrentTime(), // Установка текущих даты и времени
        conductorId = this.conductorId
    )
}