package com.chaikasoft.app.data.room.mappers

import com.chaikasoft.app.data.room.entities.CartOperation
import com.chaikasoft.app.data.room.relations.CartOperationWithConductor
import com.chaikasoft.app.domain.models.CartOperationDomain
import com.chaikasoft.app.domain.models.OperationTypeDomain
import com.chaikasoft.app.domain.models.report.CartOperationReport
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

// Преобразование Int в OperationType с использованием ordinal
fun Int.toOperationType(): OperationTypeDomain = OperationTypeDomain.entries.getOrNull(this)
    ?: throw IllegalArgumentException("Unknown OperationType for index $this")

// Преобразование OperationType в Int с использованием ordinal
fun OperationTypeDomain.toInt(): Int = this.ordinal

// Утилита для получения текущего времени
fun getCurrentTime(): String = DateTimeFormatter.ISO_INSTANT.format(
    Instant.now().truncatedTo(ChronoUnit.SECONDS)
)

fun CartOperation.toDomain(): CartOperationDomain = CartOperationDomain(
    operationTypeDomain = this.operationType.toOperationType(),
    conductorId = this.conductorId
)

fun CartOperationDomain.toEntity(): CartOperation = CartOperation(
    id = 0, // Система автоматически назначит ID
    operationType = this.operationTypeDomain.toInt(), // Используем ordinal для преобразования в Int
    operationTime = getCurrentTime(), // Установка текущих даты и времени
    conductorId = this.conductorId
)

/** Новый «правильный» отчётный маппинг с внешним employee_id из Relation. */
fun CartOperationWithConductor.toReportHeader(): CartOperationReport {
    val employeeId = conductor?.employeeID ?: operation.conductorId.toString()
    return CartOperationReport(
        employeeID = employeeId,
        operationType = operation.operationType,
        operationTime = operation.operationTime,
        items = emptyList() // см. пояснение ниже
    )
}

/** Помощник для сохранения старого контракта репозитория (id + шапка). */
fun CartOperationWithConductor.toReportPair(): Pair<Int, CartOperationReport> =
    operation.id to this.toReportHeader()
