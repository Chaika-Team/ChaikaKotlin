package com.chaikasoft.app.domain.models.report

/**
 * Промежуточная модель заголовка операции для сборки итогового отчёта смены.
 *
 * Эта модель не сериализуется напрямую в JSON. Она переносит только те поля операции,
 * которые нужны `GetCartReportsUseCase` до дозагрузки товарных строк по operation id.
 *
 * @param cartId составной идентификатор операции в контракте отчёта: проводник + время операции.
 * @param operationType тип операции: ADD, SOLD_CASH, SOLD_CART или REPLENISH в виде кода.
 */
data class CartOperationReportHeader(val cartId: CartIdReport, val operationType: Int)
