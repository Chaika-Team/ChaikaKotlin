package com.chaikasoft.app.data.room.mappers

import com.chaikasoft.app.data.room.entities.CartOperation
import com.chaikasoft.app.data.room.relations.CartOperationWithConductor
import com.chaikasoft.app.domain.models.CartOperationDomain
import com.chaikasoft.app.domain.models.OperationTypeDomain
import com.chaikasoft.app.domain.models.report.CartIdReport
import com.chaikasoft.app.domain.models.report.CartOperationReportHeader
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/** Преобразование сохранённого Int-кода операции в доменный enum. */
fun Int.toOperationType(): OperationTypeDomain = OperationTypeDomain.entries.getOrNull(this)
    ?: throw IllegalArgumentException("Unknown OperationType for index $this")

/** Преобразование доменного enum операции в Int-код, который хранится в Room. */
fun OperationTypeDomain.toInt(): Int = this.ordinal

/** Возвращает текущее время операции в ISO 8601 с точностью до секунды. */
fun getCurrentTime(): String = DateTimeFormatter.ISO_INSTANT.format(
    Instant.now().truncatedTo(ChronoUnit.SECONDS)
)

/** Entity операции корзины -> доменная модель операции. */
fun CartOperation.toDomain(): CartOperationDomain = CartOperationDomain(
    operationTypeDomain = this.operationType.toOperationType(),
    conductorId = this.conductorId
)

/** Доменная операция -> Room entity для новой операции. */
fun CartOperationDomain.toEntity(): CartOperation = CartOperation(
    id = 0,
    operationType = this.operationTypeDomain.toInt(),
    operationTime = getCurrentTime(),
    conductorId = this.conductorId
)

/**
 * Собирает заголовок операции для отчёта.
 *
 * Товарные строки намеренно не входят в header: они загружаются отдельно по id операции,
 * чтобы `GetCartReportsUseCase` мог собрать финальный `CartReport`.
 */
fun CartOperationWithConductor.toReportHeader(): CartOperationReportHeader {
    val employeeId = conductor?.employeeID
        ?: throw IllegalStateException(
            "Conductor not found for operationId=${operation.id}, conductorId=${operation.conductorId}"
        )
    return CartOperationReportHeader(
        cartId = CartIdReport(
            employeeId = employeeId,
            operationTime = operation.operationTime
        ),
        operationType = operation.operationType
    )
}

/** Возвращает пару: внутренний Room id операции + заголовок этой операции для отчёта. */
fun CartOperationWithConductor.toReportPair(): Pair<Int, CartOperationReportHeader> =
    operation.id to this.toReportHeader()
