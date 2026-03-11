package com.chaikasoft.app.domain.usecases.conductorTripShiftUseCases

import com.chaikasoft.app.data.room.repo.RoomConductorTripShiftRepositoryInterface
import com.chaikasoft.app.domain.models.report.CartIdReport
import com.chaikasoft.app.domain.models.report.CartItemReport
import com.chaikasoft.app.domain.models.report.CartReport
import com.chaikasoft.app.domain.models.report.ShiftReportReport
import com.chaikasoft.app.domain.models.report.TripIdReport
import com.chaikasoft.app.domain.models.trip.CarriageDomain
import com.chaikasoft.app.domain.models.trip.ConductorTripShiftDomain
import com.chaikasoft.app.domain.models.trip.StationDomain
import com.chaikasoft.app.domain.models.trip.TripDomain
import com.chaikasoft.app.domain.models.trip.TripShiftStatusDomain
import com.chaikasoft.app.domain.usecases.ClearOperationsAndPackageUseCase
import com.chaikasoft.app.domain.usecases.GenerateShiftReportUseCase
import com.chaikasoft.app.domain.usecases.GetCartReportsUseCase
import com.squareup.moshi.Moshi
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest

class GenerateShiftReportUseCaseTest : FunSpec({

    lateinit var shiftRepo: RoomConductorTripShiftRepositoryInterface
    lateinit var getCartReports: GetCartReportsUseCase
    lateinit var clearOpsAndPackage: ClearOperationsAndPackageUseCase
    lateinit var useCase: GenerateShiftReportUseCase
    lateinit var moshi: Moshi

    val uuid = "trip-uuid-123"
    val fromStation = StationDomain(code = "STA", name = "Start", city = "CityA")
    val toStation = StationDomain(code = "STB", name = "End", city = "CityB")
    val trip = TripDomain(
        uuid = uuid,
        trainNumber = "TN-01",
        departure = "2025-01-01T00:00:00Z",
        arrival = "2025-01-01T10:00:00Z",
        duration = "PT10H",
        from = fromStation,
        to = toStation
    )
    val carriage = CarriageDomain(carNumber = "12", classType = "2A")
    val carts = listOf(
        CartReport(
            cartId = CartIdReport(
                employeeId = "emp-123",
                operationTime = "2025-01-01T02:00:00Z"
            ),
            operationType = 1,
            items = listOf(
                CartItemReport(
                    productId = 1001,
                    quantity = 2,
                    price = 150
                )
            )
        )
    )

    // Analog of @BeforeEach: runs before each test(...)
    beforeTest {
        shiftRepo = mockk()
        getCartReports = mockk()
        clearOpsAndPackage = mockk()
        moshi = Moshi.Builder().build()
        useCase = GenerateShiftReportUseCase(
            shiftRepo = shiftRepo,
            getCartReports = getCartReports,
            moshi = moshi,
            clearOpsAndPackage = clearOpsAndPackage
        )
    }

    /**
     * Test-design technique: #1 Equivalence classes
     *
     * Author: OwletsFox
     *
     * Description:
     *   - Equivalent class: shift is missing by uuid.
     *   - Expected behavior:
     *       1) use case throws IllegalStateException,
     *       2) does not fetch cart reports or write status/report.
     *   - Goal: protect invariant "cannot generate report for missing shift".
     */
    test("when shift is missing - throws and does not write report") {
        runTest {
            coEvery { shiftRepo.getShiftByUuid(uuid) } returns null

            shouldThrow<IllegalStateException> {
                useCase(uuid)
            }

            coVerify(exactly = 1) { shiftRepo.getShiftByUuid(uuid) }
            coVerify(exactly = 0) { getCartReports() }
            coVerify(exactly = 0) { shiftRepo.updateStatusAndReport(any(), any(), any(), any()) }
            coVerify(exactly = 0) { clearOpsAndPackage() }
            confirmVerified(shiftRepo, getCartReports, clearOpsAndPackage)
        }
    }

    /**
     * Test-design technique: #1 Equivalence classes
     *
     * Author: OwletsFox
     *
     * Description:
     *   - Equivalent class: shift exists but status is not ACTIVE.
     *   - Expected behavior:
     *       1) use case throws IllegalStateException,
     *       2) does not fetch cart reports or write status/report.
     *   - Goal: protect invariant "only ACTIVE shift can be finished".
     */
    test("when status is not ACTIVE - throws and does not write report") {
        runTest {
            val shift = ConductorTripShiftDomain(
                trip = trip,
                activeCarriage = carriage,
                status = TripShiftStatusDomain.FINISHED
            )
            coEvery { shiftRepo.getShiftByUuid(uuid) } returns shift

            shouldThrow<IllegalStateException> {
                useCase(uuid)
            }

            coVerify(exactly = 1) { shiftRepo.getShiftByUuid(uuid) }
            coVerify(exactly = 0) { getCartReports() }
            coVerify(exactly = 0) { shiftRepo.updateStatusAndReport(any(), any(), any(), any()) }
            coVerify(exactly = 0) { clearOpsAndPackage() }
            confirmVerified(shiftRepo, getCartReports, clearOpsAndPackage)
        }
    }

    /**
     * Test-design technique: #2 Boundary values
     *
     * Author: OwletsFox
     *
     * Description:
     *   - Boundary case: ACTIVE shift without active carriage (null).
     *   - Expected behavior:
     *       1) use case throws IllegalStateException,
     *       2) does not write status/report or clear operations.
     *   - Goal: guard against generating reports with missing carriage id.
     */
    test("when active carriage is missing - throws and does not write report") {
        runTest {
            val shift = ConductorTripShiftDomain(
                trip = trip,
                activeCarriage = null,
                status = TripShiftStatusDomain.ACTIVE
            )
            coEvery { shiftRepo.getShiftByUuid(uuid) } returns shift
            coEvery { getCartReports() } returns carts

            shouldThrow<IllegalStateException> {
                useCase(uuid)
            }

            coVerify(exactly = 1) { shiftRepo.getShiftByUuid(uuid) }
            coVerify(exactly = 0) { shiftRepo.updateStatusAndReport(any(), any(), any(), any()) }
            coVerify(exactly = 0) { clearOpsAndPackage() }
        }
    }

    /**
     * Test-design technique: #7 Decision table
     *
     * Author: OwletsFox
     *
     * Description:
     *   - Conditions: shift is ACTIVE, carriage is set, carts are present.
     *   - Expected behavior:
     *       1) returns generated JSON,
     *       2) writes FINISHED status with report JSON,
     *       3) clears operations after persisting report.
     *   - Goal: verify the happy-path transition ACTIVE -> FINISHED with report payload.
     */
    test("when shift is ACTIVE - generates json, updates status, and clears operations") {
        runTest {
            val shift = ConductorTripShiftDomain(
                trip = trip,
                activeCarriage = carriage,
                status = TripShiftStatusDomain.ACTIVE
            )
            val statusSlot = slot<Int>()
            val reportSlot = slot<String>()

            coEvery { shiftRepo.getShiftByUuid(uuid) } returns shift
            coEvery { getCartReports() } returns carts
            coEvery {
                shiftRepo.updateStatusAndReport(
                    uuid = uuid,
                    newStatus = capture(statusSlot),
                    reportJson = capture(reportSlot),
                    updatedAt = any()
                )
            } returns Unit
            coEvery { clearOpsAndPackage() } returns Unit

            val result = useCase(uuid)

            result shouldBe reportSlot.captured
            statusSlot.captured shouldBe TripShiftStatusDomain.FINISHED.code

            val expectedReport = ShiftReportReport(
                tripId = TripIdReport(routeId = trip.trainNumber, startTime = trip.departure),
                endTime = trip.arrival,
                carriageId = carriage.carNumber.toInt(),
                carts = carts
            )
            val adapter = moshi.adapter(ShiftReportReport::class.java)
            val parsed = adapter.fromJson(reportSlot.captured)
            parsed.shouldNotBeNull()
            parsed shouldBe expectedReport

            coVerifyOrder {
                shiftRepo.getShiftByUuid(uuid)
                getCartReports()
                shiftRepo.updateStatusAndReport(uuid, any(), any(), any())
                clearOpsAndPackage()
            }
            confirmVerified(shiftRepo, getCartReports, clearOpsAndPackage)
        }
    }
})
