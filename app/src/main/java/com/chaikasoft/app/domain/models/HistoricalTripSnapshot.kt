package com.chaikasoft.app.domain.models

/**
 * Read-only snapshot of a finished trip restored from the persisted shift report JSON.
 *
 * The snapshot is intentionally detached from live cart tables: historical screens must show
 * exactly what was saved in `conductor_trip_shifts.report`.
 */
data class HistoricalTripSnapshot(
    val statistics: List<FastReportDomain>,
    val cashRevenue: Int,
    val cashlessChecksCount: Int,
    val operations: List<HistoricalOperationDomain>
)

/**
 * Operation row restored from a historical report with its already resolved cart items.
 */
data class HistoricalOperationDomain(val summary: OperationSummaryDomain, val cart: CartDomain)
