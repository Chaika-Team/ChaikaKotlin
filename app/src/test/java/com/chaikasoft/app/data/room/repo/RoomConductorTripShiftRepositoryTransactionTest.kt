package com.chaikasoft.app.data.room.repo

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.chaikasoft.app.data.room.AppDatabase
import com.chaikasoft.app.data.room.dao.CartOperationDao
import com.chaikasoft.app.data.room.entities.CartItem
import com.chaikasoft.app.data.room.entities.CartOperation
import com.chaikasoft.app.data.room.entities.Conductor
import com.chaikasoft.app.data.room.entities.ConductorTripShift
import com.chaikasoft.app.data.room.entities.ProductInfo
import com.chaikasoft.app.data.room.entities.Station
import com.chaikasoft.app.domain.models.OperationTypeDomain
import com.chaikasoft.app.domain.models.trip.CarriageDomain
import com.chaikasoft.app.domain.models.trip.ConductorTripShiftDomain
import com.chaikasoft.app.domain.models.trip.StationDomain
import com.chaikasoft.app.domain.models.trip.TripDomain
import com.chaikasoft.app.domain.models.trip.TripShiftStatusDomain
import com.chaikasoft.app.domain.sealed.StartShiftResult
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
class RoomConductorTripShiftRepositoryTransactionTest {

    private lateinit var db: AppDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        db.openHelper.writableDatabase.execSQL(
            """
            CREATE UNIQUE INDEX IF NOT EXISTS idx_unique_active_shift
            ON conductor_trip_shifts(status)
            WHERE status = ${TripShiftStatusDomain.ACTIVE.code}
            """.trimIndent()
        )
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun deleteActiveShift_withoutClearingOperations_preservesPackage() = runTest {
        seedActiveShift()
        seedOperationWithItem()

        createRepository().deleteActiveShift(UUID, clearOperations = false)

        assertNull(db.conductorTripShiftDao().getByUuid(UUID))
        assertEquals(1, db.cartOperationDao().getAllOperations().first().size)
        assertEquals(1, db.cartItemDao().getAllCartItems().first().size)
    }

    @Test
    fun tryStartNewShift_freshTrip_returnsStarted() = runTest {
        seedStations()

        val result = createRepository().tryStartNewShift(sampleDomainShift(UUID))

        assertEquals(StartShiftResult.Started, result)
        assertNotNull(db.conductorTripShiftDao().getByUuid(UUID))
    }

    @Test
    fun tryStartNewShift_activeShiftExists_returnsActiveConflict() = runTest {
        seedActiveShift()

        val result = createRepository().tryStartNewShift(sampleDomainShift("another-trip"))

        assertEquals(StartShiftResult.ActiveShiftAlreadyExists, result)
        assertNull(db.conductorTripShiftDao().getByUuid("another-trip"))
    }

    @Test
    fun tryStartNewShift_finishedTripWithSameUuid_returnsRegisteredConflict() = runTest {
        seedActiveShift()
        db.conductorTripShiftDao().updateStatusAndReport(
            uuid = UUID,
            newStatus = TripShiftStatusDomain.FINISHED.code,
            reportJson = "{}",
            updatedAt = 2L
        )

        val result = createRepository().tryStartNewShift(sampleDomainShift(UUID))

        assertEquals(StartShiftResult.TripAlreadyRegistered, result)
        val history = db.conductorTripShiftDao().getHistoryWithStations().first()
        assertEquals(1, history.size)
        assertEquals(TripShiftStatusDomain.FINISHED.code, history.single().shift.status)
        assertEquals("{}", history.single().shift.report)
    }

    @Test
    fun tryStartNewShift_unknownConstraint_rethrows() = runTest {
        seedStations()
        val invalidShift = sampleDomainShift(UUID).copy(
            trip = sampleDomainShift(UUID).trip.copy(
                from = StationDomain(code = "MISSING", name = "Missing", city = "Missing")
            )
        )

        try {
            createRepository().tryStartNewShift(invalidShift)
            fail("Expected SQLiteConstraintException")
        } catch (_: SQLiteConstraintException) {
        }
    }

    @Test
    fun deleteActiveShift_withClearingOperations_removesPackage() = runTest {
        seedActiveShift()
        seedOperationWithItem()

        createRepository().deleteActiveShift(UUID, clearOperations = true)

        assertNull(db.conductorTripShiftDao().getByUuid(UUID))
        assertEquals(emptyList<CartOperation>(), db.cartOperationDao().getAllOperations().first())
        assertEquals(emptyList<CartItem>(), db.cartItemDao().getAllCartItems().first())
    }

    @Test
    fun deleteActiveShift_finishedShift_throwsAndPreservesShift() = runTest {
        seedActiveShift(status = TripShiftStatusDomain.FINISHED)

        try {
            createRepository().deleteActiveShift(UUID, clearOperations = true)
            fail("Expected IllegalStateException")
        } catch (_: IllegalStateException) {
        }

        assertNotNull(db.conductorTripShiftDao().getByUuid(UUID))
    }

    @Test
    fun deleteActiveShift_clearFailure_rollsBackShiftDeletion() = runTest {
        seedActiveShift()
        seedOperationWithItem()
        val failingCartOperationDao = spyk(db.cartOperationDao())
        coEvery {
            failingCartOperationDao.clearAllOperations()
        } throws IllegalStateException("clear failed")

        try {
            createRepository(failingCartOperationDao).deleteActiveShift(UUID, clearOperations = true)
            fail("Expected IllegalStateException")
        } catch (_: IllegalStateException) {
        }

        assertNotNull(db.conductorTripShiftDao().getByUuid(UUID))
        assertEquals(1, db.cartOperationDao().getAllOperations().first().size)
        assertEquals(1, db.cartItemDao().getAllCartItems().first().size)
    }

    private fun createRepository(
        cartOperationDao: CartOperationDao = db.cartOperationDao()
    ): RoomConductorTripShiftRepository = RoomConductorTripShiftRepository(
        db = db,
        dao = db.conductorTripShiftDao(),
        cartOperationDao = cartOperationDao
    )

    private suspend fun seedActiveShift(status: TripShiftStatusDomain = TripShiftStatusDomain.ACTIVE) {
        seedStations()
        db.conductorTripShiftDao().insertNew(
            ConductorTripShift(
                uuid = UUID,
                trainNumber = "TN-01",
                departure = "2025-01-01T00:00:00Z",
                arrival = "2025-01-01T10:00:00Z",
                duration = "PT10H",
                fromCode = "STA",
                toCode = "STB",
                activeCarriage = CarriageDomain("12", "2A"),
                status = status.code,
                report = null,
                createdAt = 1L,
                updatedAt = null
            )
        )
    }

    private suspend fun seedStations() {
        db.stationDao().upsertAll(
            listOf(
                Station(code = "STA", name = "Start", city = "CityA"),
                Station(code = "STB", name = "End", city = "CityB")
            )
        )
    }

    private fun sampleDomainShift(uuid: String) = ConductorTripShiftDomain(
        trip = TripDomain(
            uuid = uuid,
            trainNumber = "TN-01",
            departure = "2025-01-01T00:00:00Z",
            arrival = "2025-01-01T10:00:00Z",
            duration = "PT10H",
            from = StationDomain(code = "STA", name = "Start", city = "CityA"),
            to = StationDomain(code = "STB", name = "End", city = "CityB")
        ),
        activeCarriage = CarriageDomain("12", "2A"),
        status = TripShiftStatusDomain.ACTIVE
    )

    private suspend fun seedOperationWithItem() {
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
                id = 100,
                name = "Tea",
                description = "Black",
                image = "img",
                price = 150
            )
        )
        val operationId = db.cartOperationDao().insertOperation(
            CartOperation(
                operationType = OperationTypeDomain.ADD.ordinal,
                operationTime = "2025-01-01T02:00:00Z",
                conductorId = 7
            )
        ).toInt()
        db.cartItemDao().insertCartItem(
            CartItem(
                cartOperationId = operationId,
                productId = 100,
                impact = 3
            )
        )
    }

    private companion object {
        const val UUID = "trip-delete-uuid"
    }
}
