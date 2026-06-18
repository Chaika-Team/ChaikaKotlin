package com.chaikasoft.app.domain.mappers

import com.chaikasoft.app.domain.models.CartDomain
import com.chaikasoft.app.domain.models.CartItemDomain
import com.chaikasoft.app.domain.models.ConductorDomain
import com.chaikasoft.app.domain.models.FastReportDomain
import com.chaikasoft.app.domain.models.HistoricalOperationDomain
import com.chaikasoft.app.domain.models.HistoricalTripSnapshot
import com.chaikasoft.app.domain.models.OperationSummaryDomain
import com.chaikasoft.app.domain.models.OperationTypeDomain
import com.chaikasoft.app.domain.models.ProductInfoDomain
import com.chaikasoft.app.domain.models.report.CartItemReport
import com.chaikasoft.app.domain.models.report.CartReport
import com.chaikasoft.app.domain.models.report.ShiftReportReport
import com.chaikasoft.app.util.toZoned
import kotlin.math.abs

private const val UNKNOWN_PRODUCT_DESCRIPTION = ""
private const val UNKNOWN_PRODUCT_IMAGE = ""

/**
 * Maps persisted shift report JSON model into read-only historical screen data.
 *
 * Product price and quantities are taken from the report snapshot. Product and conductor names are
 * resolved from current local dictionaries; missing names stay empty and are localized in UI.
 */
suspend fun ShiftReportReport.toHistoricalTripSnapshot(
    productsById: Map<Int, ProductInfoDomain>,
    resolveConductor: suspend (String) -> ConductorDomain
): HistoricalTripSnapshot {
    val operations = carts.toNewestFirstHistoricalOperations(
        productsById = productsById,
        resolveConductor = resolveConductor
    )
    val statistics = carts.toHistoricalStatistics(productsById)

    return HistoricalTripSnapshot(
        statistics = statistics,
        cashRevenue = statistics.sumOf { it.revenue },
        cashlessChecksCount = carts.count {
            it.operationType == OperationTypeDomain.SOLD_CARD.ordinal
        },
        operations = operations
    )
}

private suspend fun List<CartReport>.toNewestFirstHistoricalOperations(
    productsById: Map<Int, ProductInfoDomain>,
    resolveConductor: suspend (String) -> ConductorDomain
): List<HistoricalOperationDomain> =
    mapIndexed { index, cart -> IndexedCartReport(index = index, cart = cart) }
        .sortedWith(
            compareByDescending<IndexedCartReport> {
                it.cart.cartId.operationTime.toZoned().toInstant()
            }.thenByDescending { it.index }
        )
        .mapIndexed { index, indexedCart ->
            indexedCart.cart.toHistoricalOperation(
                syntheticId = index + 1,
                productsById = productsById,
                resolveConductor = resolveConductor
            )
        }

private suspend fun CartReport.toHistoricalOperation(
    syntheticId: Int,
    productsById: Map<Int, ProductInfoDomain>,
    resolveConductor: suspend (String) -> ConductorDomain
): HistoricalOperationDomain {
    val operationType = operationType.toHistoricalOperationType()
    val cartDomain = CartDomain(
        items = items.map { it.toDomainItem(productsById) }
    )
    val summary = OperationSummaryDomain(
        id = syntheticId,
        type = operationType,
        timeIso = cartId.operationTime,
        conductor = resolveConductor(cartId.employeeId),
        productLineQuantity = items.map { it.productId }.distinct().size,
        totalPrice = items.sumOf { abs(it.quantity) * it.price }
    )

    return HistoricalOperationDomain(
        summary = summary,
        cart = cartDomain
    )
}

private fun List<CartReport>.toHistoricalStatistics(
    productsById: Map<Int, ProductInfoDomain>
): List<FastReportDomain> {
    val accumulators = linkedMapOf<Int, HistoricalProductAccumulator>()
    forEach { cart ->
        val operationType = cart.operationType.toHistoricalOperationType()
        cart.items.forEach { item ->
            val accumulator = accumulators.getOrPut(item.productId) {
                HistoricalProductAccumulator.from(
                    item = item,
                    product = productsById[item.productId]
                )
            }
            accumulator.add(item, operationType)
        }
    }

    return accumulators.values.map { it.toDomain() }
}

private data class IndexedCartReport(val index: Int, val cart: CartReport)

private fun Int.toHistoricalOperationType(): OperationTypeDomain =
    OperationTypeDomain.entries.getOrNull(this)
        ?: throw IllegalArgumentException("Unknown operation type $this")

private fun CartItemReport.toDomainItem(productsById: Map<Int, ProductInfoDomain>): CartItemDomain {
    val product = productsById[productId]
    return CartItemDomain(
        product = ProductInfoDomain(
            id = productId,
            name = product?.name.orEmpty(),
            description = product?.description ?: UNKNOWN_PRODUCT_DESCRIPTION,
            image = product?.image ?: UNKNOWN_PRODUCT_IMAGE,
            price = price
        ),
        quantity = quantity
    )
}

private data class HistoricalProductAccumulator(
    val productId: Int,
    val productName: String,
    val productPrice: Int,
    var addedQuantity: Int = 0,
    var replenishedQuantity: Int = 0,
    var soldCashQuantity: Int = 0,
    var soldCardQuantity: Int = 0,
    var revenue: Int = 0
) {
    fun add(item: CartItemReport, operationType: OperationTypeDomain) {
        when (operationType) {
            OperationTypeDomain.ADD -> addedQuantity += item.quantity
            OperationTypeDomain.REPLENISH -> replenishedQuantity += item.quantity
            OperationTypeDomain.SOLD_CASH -> {
                val soldQuantity = -item.quantity
                soldCashQuantity += soldQuantity
                revenue += soldQuantity * item.price
            }
            OperationTypeDomain.SOLD_CARD -> {
                soldCardQuantity += -item.quantity
            }
        }
    }

    fun toDomain(): FastReportDomain = FastReportDomain(
        productName = productName,
        productPrice = productPrice,
        addedQuantity = addedQuantity,
        replenishedQuantity = replenishedQuantity,
        soldCashQuantity = soldCashQuantity,
        soldCardQuantity = soldCardQuantity,
        revenue = revenue,
        productId = productId
    )

    companion object {
        fun from(item: CartItemReport, product: ProductInfoDomain?): HistoricalProductAccumulator =
            HistoricalProductAccumulator(
                productId = item.productId,
                productName = product?.name.orEmpty(),
                productPrice = item.price
            )
    }
}
