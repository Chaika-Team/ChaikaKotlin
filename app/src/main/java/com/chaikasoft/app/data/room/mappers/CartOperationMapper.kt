package com.chaikasoft.app.data.room.mappers

import com.chaikasoft.app.data.room.entities.CartOperation
import com.chaikasoft.app.domain.models.CartOperationDomain
import com.chaikasoft.app.domain.models.report.CartOperationReport
import com.chaikasoft.app.domain.models.OperationTypeDomain
import com.chaikasoft.app.domain.models.report.CartItemReport
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

// Преобразование Int в OperationType с использованием ordinal
fun Int.toOperationType(): OperationTypeDomain =
    OperationTypeDomain.entries.getOrNull(this)
        ?: throw IllegalArgumentException("Unknown OperationType for index $this")

// Преобразование OperationType в Int с использованием ordinal
fun OperationTypeDomain.toInt(): Int = this.ordinal

// Утилита для получения текущего времени
fun getCurrentTime(): String =
    DateTimeFormatter.ISO_INSTANT.format(
        Instant.now().truncatedTo(ChronoUnit.SECONDS)
    )

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
