package com.example.chaika.data.room.mappers

import com.example.chaika.data.room.entities.CartOperation
import com.example.chaika.domain.models.CartOperationDomain
import com.example.chaika.domain.models.report.CartOperationReport
import com.example.chaika.domain.models.OperationTypeDomain
import com.example.chaika.domain.models.report.CartItemReport
import java.text.SimpleDateFormat
import java.util.Locale

// Преобразование Int в OperationType с использованием ordinal
fun Int.toOperationType(): OperationTypeDomain =
    OperationTypeDomain.entries.getOrNull(this)
        ?: throw IllegalArgumentException("Unknown OperationType for index $this")

// Преобразование OperationType в Int с использованием ordinal
fun OperationTypeDomain.toInt(): Int = this.ordinal

// Утилита для получения текущего времени в формате ISO 8601
fun getCurrentTime(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    return dateFormat.format(System.currentTimeMillis())
}

fun CartOperation.toDomain(): CartOperationDomain =
    CartOperationDomain(
        operationTypeDomain = this.operationType.toOperationType(),
        conductorId = this.conductorId,
    )

fun CartOperationDomain.toEntity(): CartOperation =
    CartOperation(
        id = 0, // Система автоматически назначит ID
        operationType = this.operationTypeDomain.toInt(), // Используем ordinal для преобразования в Int
        operationTime = getCurrentTime(), // Установка текущих даты и времени
        conductorId = this.conductorId,
    )

// Репорт-модель для отчётов
fun CartOperation.toReport(items: List<CartItemReport>): CartOperationReport =
    CartOperationReport(
        employeeID = this.conductorId.toString(), // Временно передаём conductorId, заменим позже
        operationType = this.operationType,
        operationTime = this.operationTime,
        items = items,
    )
