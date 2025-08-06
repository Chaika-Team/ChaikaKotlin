@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.example.chaika.domain.usecases

import com.example.chaika.data.room.repo.RoomCartItemRepositoryInterface
import com.example.chaika.data.room.repo.RoomCartOperationRepositoryInterface
import com.example.chaika.data.room.repo.RoomConductorTripShiftRepositoryInterface
import com.example.chaika.domain.models.report.CartItemReport
import com.example.chaika.domain.models.report.CartOperationReport
import com.example.chaika.domain.models.trip.CarriageDomain
import com.example.chaika.domain.models.trip.ConductorTripShiftDomain
import com.example.chaika.domain.models.trip.StationDomain
import com.example.chaika.domain.models.trip.TripDomain
import com.example.chaika.domain.models.trip.TripShiftStatusDomain
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.isNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class ConductorTripShiftUseCasesTest {

    @Mock
    lateinit var shiftRepo: RoomConductorTripShiftRepositoryInterface

    @Mock
    lateinit var cartOpRepo: RoomCartOperationRepositoryInterface

    @Mock
    lateinit var cartItemRepo: RoomCartItemRepositoryInterface

    // Настоящий Moshi для сериализации
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    // dummy domain модели маршрута и вагона
    private val fromStation = StationDomain(code = 1, name = "A", city = "CityA")
    private val toStation   = StationDomain(code = 2, name = "B", city = "CityB")
    private val trip = TripDomain(
        uuid        = "uuid-123",
        trainNumber = "TN-01",
        departure   = "2025-01-01T00:00:00Z",
        arrival     = "2025-01-01T10:00:00Z",
        duration    = "PT10H",
        from        = fromStation,
        to          = toStation
    )
    private val carriage = CarriageDomain(carNumber = "01", classType = "2Я")

    // dummy report для операций и товаров
    private val dummyCartItem = CartItemReport(
        productId = 101,
        quantity  = 2,
        price     = 150.0
    )
    private val dummyCartOp = CartOperationReport(
        employeeID    = "emp123",
        operationType = 1,
        operationTime = "2025-08-05T19:00:00Z",
        items         = listOf(dummyCartItem)
    )
    private val dummyOpList = listOf(1 to dummyCartOp)

    /**
     * GetAllShiftsUseCase должен просто делегировать repo.observeAllShifts()
     */
    @Test
    fun `GetAllShiftsUseCase returns flow from repository`() {
        val dummyShifts = listOf(
            ConductorTripShiftDomain(trip, carriage, TripShiftStatusDomain.ACTIVE)
        )
        whenever(shiftRepo.observeAllShifts()).thenReturn(flowOf(dummyShifts))

        val useCase = GetAllShiftsUseCase(shiftRepo)
        runTest {
            val result = useCase().first()
            assertEquals(dummyShifts, result)
        }
    }

    /**
     * GetActiveShiftUseCase должен просто делегировать repo.observeActiveShift()
     */
    @Test
    fun `GetActiveShiftUseCase returns flow from repository`() {
        val active = ConductorTripShiftDomain(trip, null, TripShiftStatusDomain.ACTIVE)
        whenever(shiftRepo.observeActiveShift()).thenReturn(flowOf(active))

        val useCase = GetActiveShiftUseCase(shiftRepo)
        runTest {
            val result = useCase().first()
            assertEquals(active, result)
        }
    }

    /**
     * StartShiftUseCase не должен создавать новую смену, если уже есть активная
     */
    @Test
    fun `StartShiftUseCase returns false when active shift exists`() = runTest {
        whenever(shiftRepo.getActiveShift()).thenReturn(
            ConductorTripShiftDomain(trip, null, TripShiftStatusDomain.ACTIVE)
        )
        val useCase = StartShiftUseCase(shiftRepo)
        val created = useCase(trip, carriage)
        assertFalse(created)
        verify(shiftRepo, never()).insertOrUpdate(any())
    }

    /**
     * StartShiftUseCase создаёт новую смену, если активной ещё нет
     */
    @Test
    fun `StartShiftUseCase returns true and inserts when no active shift`() = runTest {
        whenever(shiftRepo.getActiveShift()).thenReturn(null)

        val useCase = StartShiftUseCase(shiftRepo)
        val created = useCase(trip, carriage)
        assertTrue(created)

        val captor = argumentCaptor<ConductorTripShiftDomain>()
        verify(shiftRepo).insertOrUpdate(captor.capture())
        val inserted = captor.firstValue
        assertEquals(trip, inserted.trip)
        assertEquals(carriage, inserted.activeCarriage)
        assertEquals(TripShiftStatusDomain.ACTIVE, inserted.status)
    }

    /**
     * GenerateShiftReportUseCase должен сгенерировать корректный JSON-отчёт и вызвать обновление статуса
     */
    @Test
    fun `GenerateShiftReportUseCase generates report and updates status to FINISHED`() = runTest {
        val uuid = trip.uuid
        // 1) Активная смена
        whenever(shiftRepo.getActiveShift()).thenReturn(
            ConductorTripShiftDomain(trip, carriage, TripShiftStatusDomain.ACTIVE)
        )
        // 2) Операции
        whenever(cartOpRepo.getCartOperationReportsWithIds())
            .thenReturn(flowOf(dummyOpList))
        // 3) Товары для opId=1
        whenever(cartItemRepo.getCartItemReportsByOperationId(1))
            .thenReturn(flowOf(listOf(dummyCartItem)))

        val useCase = GenerateShiftReportUseCase(
            shiftRepo, cartOpRepo, cartItemRepo, moshi
        )
        val json = useCase.invoke(uuid)

        // проверяем часть JSON
        assertTrue(json.contains("\"route_id\":\"TN-01\""))
        assertTrue(json.contains("\"end_time\":\"2025-01-01T10:00:00Z\""))
        assertTrue(json.contains("\"carriage_id\":1"))
        assertTrue(json.contains("\"employee_id\":\"emp123\""))
        assertTrue(json.contains("\"operation_type\":1"))
        assertTrue(json.contains("\"product_id\":101"))

        // проверяем вызов обновления статуса и сохранения JSON
        verify(shiftRepo).updateStatusAndReport(
            eq(uuid),
            eq(TripShiftStatusDomain.FINISHED.ordinal),
            eq(json),
            any()
        )
    }

    /**
     * GenerateShiftReportUseCase кидает IllegalStateException, когда нет активной смены
     */
    @Test
    fun `GenerateShiftReportUseCase throws when no active shift`() = runTest {
        whenever(shiftRepo.getActiveShift()).thenReturn(null)

        val useCase = GenerateShiftReportUseCase(
            shiftRepo, cartOpRepo, cartItemRepo, moshi
        )
        assertThrows<IllegalStateException> {
            useCase.invoke("missing-uuid")
        }
    }

    /**
     * SendShiftReportUseCase должен пометить смену как SENT и вернуть true
     */
    @Test
    fun `SendShiftReportUseCase updates status to SENT and returns true`() = runTest {
        val useCase = SendShiftReportUseCase(shiftRepo)
        val uuid = trip.uuid

        val result = useCase(uuid)
        assertTrue(result)

        verify(shiftRepo).updateStatusAndReport(
            eq(uuid),
            eq(TripShiftStatusDomain.SENT.ordinal),
            isNull(),
            any()
        )
    }

    /**
     * CompleteShiftUseCase должен вызвать сначала генерацию, потом отправку, и вернуть true
     */
    @Test
    fun `CompleteShiftUseCase invokes generate then send and returns send result`() = runTest {
        val gen = mock<GenerateShiftReportUseCase>()
        val snd = mock<SendShiftReportUseCase>()
        val uuid = trip.uuid
        whenever(gen.invoke(uuid)).thenReturn("{\"dummy\":true}")
        whenever(snd.invoke(uuid)).thenReturn(true)

        val useCase = CompleteShiftUseCase(gen, snd)
        val result = useCase.invoke(uuid)
        assertTrue(result)

        inOrder(gen, snd) {
            verify(gen).invoke(uuid)
            verify(snd).invoke(uuid)
        }
    }

    /**
     * CompleteShiftUseCase когда отправка неудачна, возвращает false
     */
    @Test
    fun `CompleteShiftUseCase returns false when send fails`() = runTest {
        val gen = mock<GenerateShiftReportUseCase>()
        val snd = mock<SendShiftReportUseCase>()
        val uuid = trip.uuid
        whenever(gen.invoke(uuid)).thenReturn("{\"dummy\":true}")
        whenever(snd.invoke(uuid)).thenReturn(false)

        val useCase = CompleteShiftUseCase(gen, snd)
        val result = useCase.invoke(uuid)
        assertFalse(result)

        verify(gen).invoke(uuid)
        verify(snd).invoke(uuid)
    }
}
