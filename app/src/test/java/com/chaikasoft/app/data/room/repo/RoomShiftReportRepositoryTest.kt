package com.chaikasoft.app.data.room.repo

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.chaikasoft.app.data.room.AppDatabase
import com.chaikasoft.app.data.room.entities.CartItem
import com.chaikasoft.app.data.room.entities.CartOperation
import com.chaikasoft.app.data.room.entities.Conductor
import com.chaikasoft.app.data.room.entities.ConductorTripShift
import com.chaikasoft.app.data.room.entities.ProductInfo
import com.chaikasoft.app.data.room.entities.Station
import com.chaikasoft.app.data.room.relations.CartOperationWithConductor
import com.chaikasoft.app.domain.models.OperationTypeDomain
import com.chaikasoft.app.domain.models.report.ShiftReportReport
import com.chaikasoft.app.domain.models.trip.CarriageDomain
import com.chaikasoft.app.domain.models.trip.TripShiftStatusDomain
import com.squareup.moshi.Moshi
import io.mockk.coEvery
import io.mockk.spyk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RoomShiftReportRepositoryTest {

    private lateinit var db: AppDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        db.close()
    }

    /**
     * Test-design technique: #7 Decision table.
     * ACTIVE shift with valid operations should be finished, persisted with JSON, and cleaned up.
     */
    @Test
    fun finishShiftWithReport_success_persistsReportAndClearsOperations() = runTest {
        seedActiveShift()
        seedOperationWithItem(productId = 100, impact = -2)
        val repository = createRepository()

        val json = repository.finishShiftWithReport(UUID)

        val savedShift = db.conductorTripShiftDao().getByUuid(UUID)
        assertEquals(TripShiftStatusDomain.FINISHED.code, savedShift?.status)
        assertEquals(json, savedShift?.report)
        assertEquals(emptyList<CartOperation>(), db.cartOperationDao().getAllOperations().first())
        assertEquals(emptyList<CartItem>(), db.cartItemDao().getAllCartItems().first())

        val parsed = Moshi.Builder()
            .build()
            .adapter(ShiftReportReport::class.java)
            .fromJson(json)
        assertNotNull(parsed)
        assertEquals("TN-01", parsed?.tripId?.routeId)
        assertEquals("2025-01-01T00:00:00Z", parsed?.tripId?.startTime)
        assertEquals("2025-01-01T10:00:00Z", parsed?.endTime)
        assertEquals(12, parsed?.carriageId)
        assertEquals(1, parsed?.carts?.size)
        assertEquals("EMP-7", parsed?.carts?.first()?.cartId?.employeeId)
        assertEquals(OperationTypeDomain.SOLD_CARD.ordinal, parsed?.carts?.first()?.operationType)
        assertEquals(100, parsed?.carts?.first()?.items?.first()?.productId)
        assertEquals(-2, parsed?.carts?.first()?.items?.first()?.quantity)
        assertEquals(150, parsed?.carts?.first()?.items?.first()?.price)
    }

    /**
     * Test-design technique: #5 Error guessing.
     * Missing carriage metadata is invalid local state and must not finish or clear the shift.
     */
    @Test
    fun finishShiftWithReport_missingCarriage_rollsBackLocalState() = runTest {
        seedActiveShift(carriage = null)
        seedOperationWithItem(productId = 100, impact = 1)
        val repository = createRepository()

        try {
            repository.finishShiftWithReport(UUID)
            fail("Expected IllegalStateException")
        } catch (_: IllegalStateException) {
        }

        val savedShift = db.conductorTripShiftDao().getByUuid(UUID)
        assertEquals(TripShiftStatusDomain.ACTIVE.code, savedShift?.status)
        assertNull(savedShift?.report)
        assertEquals(1, db.cartOperationDao().getAllOperations().first().size)
        assertEquals(1, db.cartItemDao().getAllCartItems().first().size)
    }

    /**
     * Test-design technique: #5 Error guessing.
     * Missing conductor relation is invalid report state and must not be serialized as internal id.
     */
    @Test
    fun finishShiftWithReport_missingConductor_rollsBackLocalState() = runTest {
        seedActiveShift()
        val operationId = seedOperationWithItem(productId = 100, impact = 1)
        val failingCartOperationDao = spyk(db.cartOperationDao())
        coEvery { failingCartOperationDao.getOperationsWithConductorForReportOnce() } returns listOf(
            CartOperationWithConductor(
                operation = CartOperation(
                    id = operationId,
                    operationType = OperationTypeDomain.SOLD_CARD.ordinal,
                    operationTime = "2025-01-01T02:00:00Z",
                    conductorId = 7
                ),
                conductor = null
            )
        )
        val repository = createRepository(cartOperationDao = failingCartOperationDao)

        try {
            repository.finishShiftWithReport(UUID)
            fail("Expected IllegalStateException")
        } catch (_: IllegalStateException) {
        }

        val savedShift = db.conductorTripShiftDao().getByUuid(UUID)
        assertEquals(TripShiftStatusDomain.ACTIVE.code, savedShift?.status)
        assertNull(savedShift?.report)
        assertEquals(1, db.cartOperationDao().getAllOperations().first().size)
        assertEquals(1, db.cartItemDao().getAllCartItems().first().size)
    }

    /**
     * Test-design technique: #5 Error guessing.
     * If a later transaction step fails after status/report update, Room should roll back all writes.
     */
    @Test
    fun finishShiftWithReport_clearFailure_rollsBackReportAndStatus() = runTest {
        seedActiveShift()
        seedOperationWithItem(productId = 100, impact = 3)
        val failingCartOperationDao = spyk(db.cartOperationDao())
        coEvery { failingCartOperationDao.clearAllOperations() } throws IllegalStateException("clear failed")
        val repository = createRepository(cartOperationDao = failingCartOperationDao)

        try {
            repository.finishShiftWithReport(UUID)
            fail("Expected IllegalStateException")
        } catch (_: IllegalStateException) {
        }

        val savedShift = db.conductorTripShiftDao().getByUuid(UUID)
        assertEquals(TripShiftStatusDomain.ACTIVE.code, savedShift?.status)
        assertNull(savedShift?.report)
        assertEquals(1, db.cartOperationDao().getAllOperations().first().size)
        assertEquals(1, db.cartItemDao().getAllCartItems().first().size)
    }

    private fun createRepository(
        cartOperationDao: com.chaikasoft.app.data.room.dao.CartOperationDao = db.cartOperationDao()
    ): RoomShiftReportRepository = RoomShiftReportRepository(
        db = db,
        conductorTripShiftDao = db.conductorTripShiftDao(),
        cartOperationDao = cartOperationDao,
        cartItemDao = db.cartItemDao(),
        productInfoDao = db.productInfoDao(),
        moshi = Moshi.Builder().build()
    )

    private suspend fun seedActiveShift(carriage: CarriageDomain? = CarriageDomain("12", "2A")) {
        db.stationDao().upsertAll(
            listOf(
                Station(code = "STA", name = "Start", city = "CityA"),
                Station(code = "STB", name = "End", city = "CityB")
            )
        )
        db.conductorTripShiftDao().insertNew(
            ConductorTripShift(
                uuid = UUID,
                trainNumber = "TN-01",
                departure = "2025-01-01T00:00:00Z",
                arrival = "2025-01-01T10:00:00Z",
                duration = "PT10H",
                fromCode = "STA",
                toCode = "STB",
                activeCarriage = carriage,
                status = TripShiftStatusDomain.ACTIVE.code,
                report = null,
                createdAt = 1L,
                updatedAt = null
            )
        )
    }

    private suspend fun seedOperationWithItem(
        productId: Int,
        impact: Int
    ): Int {
        db.conductorDao().insertConductor(
            Conductor(
                id = 7,
                name = "Ivan",
                familyName = "Petrov",
                givenName = "Ivanovich",
                employeeID = "EMP-7",
                image = "img"
            )
        )
        db.productInfoDao().insertProduct(
            ProductInfo(
                id = productId,
                name = "Tea",
                description = "Black",
                image = "img",
                price = 150
            )
        )
        val operationId = db.cartOperationDao().insertOperation(
            CartOperation(
                operationType = OperationTypeDomain.SOLD_CARD.ordinal,
                operationTime = "2025-01-01T02:00:00Z",
                conductorId = 7
            )
        ).toInt()
        db.cartItemDao().insertCartItem(
            CartItem(
                cartOperationId = operationId,
                productId = productId,
                impact = impact
            )
        )
        return operationId
    }

    private companion object {
        const val UUID = "trip-uuid-123"
    }
}
