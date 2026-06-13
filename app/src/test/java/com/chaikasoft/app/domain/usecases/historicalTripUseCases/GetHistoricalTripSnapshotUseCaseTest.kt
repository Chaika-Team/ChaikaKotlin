package com.chaikasoft.app.domain.usecases.historicalTripUseCases

import com.chaikasoft.app.data.room.repo.RoomConductorRepositoryInterface
import com.chaikasoft.app.data.room.repo.RoomProductInfoRepositoryInterface
import com.chaikasoft.app.domain.models.OperationTypeDomain
import com.chaikasoft.app.domain.models.report.CartIdReport
import com.chaikasoft.app.domain.models.report.CartItemReport
import com.chaikasoft.app.domain.models.report.CartReport
import com.chaikasoft.app.domain.models.report.ShiftReportReport
import com.chaikasoft.app.domain.models.report.TripIdReport
import com.chaikasoft.app.domain.models.trip.TripShiftStatusDomain
import com.chaikasoft.app.domain.usecases.GetHistoricalTripSnapshotUseCase
import com.chaikasoft.app.domain.usecases.GetShiftReportJsonUseCase
import com.chaikasoft.app.domain.usecases.HistoricalTripSnapshotException
import com.chaikasoft.app.ui.conductor
import com.chaikasoft.app.ui.productInfo
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest

class GetHistoricalTripSnapshotUseCaseTest : FunSpec({

    lateinit var getShiftReportJson: GetShiftReportJsonUseCase
    lateinit var productRepository: RoomProductInfoRepositoryInterface
    lateinit var conductorRepository: RoomConductorRepositoryInterface
    lateinit var useCase: GetHistoricalTripSnapshotUseCase

    val shiftUuid = "shift-uuid"
    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    beforeTest {
        getShiftReportJson = mockk()
        productRepository = mockk()
        conductorRepository = mockk()
        useCase = GetHistoricalTripSnapshotUseCase(
            getShiftReportJson = getShiftReportJson,
            productRepository = productRepository,
            conductorRepository = conductorRepository,
            moshi = moshi
        )
    }

    test("restores statistics and operations from persisted report json") {
        runTest {
            val json = reportJson(
                carts = listOf(
                    cart(OperationTypeDomain.ADD, "123", "2026-01-01T10:00:00Z", item(1, 5, 1000)),
                    cart(
                        OperationTypeDomain.REPLENISH,
                        "123",
                        "2026-01-01T10:05:00Z",
                        item(1, 3, 1000)
                    ),
                    cart(
                        OperationTypeDomain.SOLD_CASH,
                        "123",
                        "2026-01-01T10:10:00Z",
                        item(1, -2, 1000),
                        item(2, -1, 500)
                    ),
                    cart(
                        OperationTypeDomain.SOLD_CARD,
                        "999",
                        "2026-01-01T10:15:00Z",
                        item(2, -2, 500)
                    )
                ),
                moshi = moshi
            )
            coEvery { getShiftReportJson(shiftUuid) } returns (TripShiftStatusDomain.SENT to json)
            coEvery { productRepository.getAllProductsOnce() } returns listOf(
                productInfo(id = 1, name = "Tea", price = 900)
            )
            coEvery { conductorRepository.getConductorByEmployeeID("123") } returns
                conductor().copy(employeeID = "123")
            coEvery { conductorRepository.getConductorByEmployeeID("999") } returns null

            val snapshot = useCase(shiftUuid)

            snapshot.statistics shouldHaveSize 2
            snapshot.statistics[0].productName shouldBe "Tea"
            snapshot.statistics[0].productPrice shouldBe 1000
            snapshot.statistics[0].addedQuantity shouldBe 5
            snapshot.statistics[0].replenishedQuantity shouldBe 3
            snapshot.statistics[0].soldCashQuantity shouldBe 2
            snapshot.statistics[0].soldCardQuantity shouldBe 0
            snapshot.statistics[0].revenue shouldBe 2000

            snapshot.statistics[1].productId shouldBe 2
            snapshot.statistics[1].productName shouldBe ""
            snapshot.statistics[1].productPrice shouldBe 500
            snapshot.statistics[1].soldCashQuantity shouldBe 1
            snapshot.statistics[1].soldCardQuantity shouldBe 2
            snapshot.statistics[1].revenue shouldBe 500

            snapshot.cashRevenue shouldBe 2500
            snapshot.cashlessChecksCount shouldBe 1

            snapshot.operations shouldHaveSize 4
            snapshot.operations[0].summary.id shouldBe 1
            snapshot.operations[0].summary.type shouldBe OperationTypeDomain.ADD
            snapshot.operations[0].summary.timeIso shouldBe "2026-01-01T10:00:00Z"
            snapshot.operations[0].cart.items.single().product.name shouldBe "Tea"
            snapshot.operations[0].cart.items.single().product.price shouldBe 1000
            snapshot.operations[2].summary.totalPrice shouldBe 2500
            snapshot.operations[3].summary.conductor.name shouldBe ""
            snapshot.operations[3].summary.conductor.familyName shouldBe ""
        }
    }

    test("throws when report is missing") {
        runTest {
            coEvery { getShiftReportJson(shiftUuid) } returns (TripShiftStatusDomain.FINISHED to null)

            shouldThrow<HistoricalTripSnapshotException> {
                useCase(shiftUuid)
            }
        }
    }

    test("throws when report json is malformed") {
        runTest {
            coEvery { getShiftReportJson(shiftUuid) } returns
                (TripShiftStatusDomain.FINISHED to """{"trip_id":""")

            val error = shouldThrow<HistoricalTripSnapshotException> {
                useCase(shiftUuid)
            }

            error.message shouldBe "Historical report is malformed"
        }
    }

    test("throws empty report error when report json contains null") {
        runTest {
            coEvery { getShiftReportJson(shiftUuid) } returns
                (TripShiftStatusDomain.FINISHED to "null")

            val error = shouldThrow<HistoricalTripSnapshotException> {
                useCase(shiftUuid)
            }

            error.message shouldBe "Historical report is empty"
        }
    }

    test("throws malformed report error when json structure is incompatible") {
        runTest {
            coEvery { getShiftReportJson(shiftUuid) } returns
                (TripShiftStatusDomain.FINISHED to "{}")

            val error = shouldThrow<HistoricalTripSnapshotException> {
                useCase(shiftUuid)
            }

            error.message shouldBe "Historical report is malformed"
        }
    }

    test("throws when shift status is not historical") {
        runTest {
            coEvery { getShiftReportJson(shiftUuid) } returns
                (TripShiftStatusDomain.ACTIVE to reportJson(emptyList(), moshi))

            shouldThrow<HistoricalTripSnapshotException> {
                useCase(shiftUuid)
            }
        }
    }
})

private fun reportJson(carts: List<CartReport>, moshi: Moshi): String {
    val report = ShiftReportReport(
        tripId = TripIdReport(
            routeId = "A-100",
            startTime = "2026-01-01T09:00:00Z"
        ),
        endTime = "2026-01-01T11:00:00Z",
        carriageId = 7,
        carts = carts
    )
    return checkNotNull(moshi.adapter(ShiftReportReport::class.java).toJson(report))
}

private fun cart(
    operationType: OperationTypeDomain,
    employeeId: String,
    operationTime: String,
    vararg items: CartItemReport
): CartReport = CartReport(
    cartId = CartIdReport(
        employeeId = employeeId,
        operationTime = operationTime
    ),
    operationType = operationType.ordinal,
    items = items.toList()
)

private fun item(productId: Int, quantity: Int, price: Int): CartItemReport = CartItemReport(
    productId = productId,
    quantity = quantity,
    price = price
)
