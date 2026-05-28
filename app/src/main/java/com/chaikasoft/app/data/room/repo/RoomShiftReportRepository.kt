package com.chaikasoft.app.data.room.repo

import androidx.room.withTransaction
import com.chaikasoft.app.data.room.AppDatabase
import com.chaikasoft.app.data.room.dao.CartItemDao
import com.chaikasoft.app.data.room.dao.CartOperationDao
import com.chaikasoft.app.data.room.dao.ConductorTripShiftDao
import com.chaikasoft.app.data.room.dao.ProductInfoDao
import com.chaikasoft.app.data.room.mappers.toReport
import com.chaikasoft.app.data.room.mappers.toReportHeader
import com.chaikasoft.app.domain.models.report.CartIdReport
import com.chaikasoft.app.domain.models.report.CartReport
import com.chaikasoft.app.domain.models.report.ShiftReportReport
import com.chaikasoft.app.domain.models.report.TripIdReport
import com.chaikasoft.app.domain.models.trip.TripShiftStatusDomain
import com.squareup.moshi.Moshi
import javax.inject.Inject

/**
 * Finishes a shift in local Room storage as one transaction.
 *
 * Network upload deliberately stays outside this class: this transaction only fixes the local
 * source of truth used by later send and retry attempts.
 */
class RoomShiftReportRepository @Inject constructor(
    private val db: AppDatabase,
    private val conductorTripShiftDao: ConductorTripShiftDao,
    private val cartOperationDao: CartOperationDao,
    private val cartItemDao: CartItemDao,
    private val productInfoDao: ProductInfoDao,
    moshi: Moshi
) : RoomShiftReportRepositoryInterface {

    private val jsonAdapter = moshi.adapter(ShiftReportReport::class.java)

    override suspend fun finishShiftWithReport(uuid: String): String = db.withTransaction {
        val shift = conductorTripShiftDao.getByUuidWithStations(uuid)
            ?: throw IllegalStateException("Shift with uuid=$uuid not found")

        check(shift.shift.status == TripShiftStatusDomain.ACTIVE.code) {
            "Shift uuid=$uuid is not ACTIVE (status=${TripShiftStatusDomain.withCode(
                shift.shift.status
            )})"
        }

        val carts = cartOperationDao.getOperationsWithConductorForReportOnce()
            .map { operationWithConductor ->
                val header = operationWithConductor.toReportHeader()
                val items = cartItemDao
                    .getCartItemsByCartOpIdOnce(operationWithConductor.operation.id)
                    .map { cartItem ->
                        val productInfo = productInfoDao.getProductById(cartItem.productId)
                            ?: throw IllegalArgumentException(
                                "Product not found for ID: ${cartItem.productId}"
                            )
                        cartItem.toReport(productInfo)
                    }

                CartReport(
                    cartId = CartIdReport(
                        employeeId = header.cartId.employeeId,
                        operationTime = header.cartId.operationTime
                    ),
                    operationType = header.operationType,
                    items = items
                )
            }

        val report = ShiftReportReport(
            tripId = TripIdReport(shift.shift.trainNumber, shift.shift.departure),
            endTime = shift.shift.arrival,
            carriageId = shift.shift.activeCarriage
                ?.carNumber
                ?.toIntOrNull()
                ?: throw IllegalStateException("Active carriage not set"),
            carts = carts
        )
        val json = jsonAdapter.toJson(report)

        conductorTripShiftDao.updateStatusAndReport(
            uuid = uuid,
            newStatus = TripShiftStatusDomain.FINISHED.code,
            reportJson = json,
            updatedAt = System.currentTimeMillis()
        )
        cartOperationDao.clearAllOperations()

        json
    }
}
