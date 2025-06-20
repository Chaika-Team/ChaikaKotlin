@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.example.chaika.domain.usecases

import com.example.chaika.data.room.repo.RoomConductorTripShiftRepositoryInterface
import com.example.chaika.domain.models.trip.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

import org.mockito.Mock
import org.mockito.kotlin.*
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class ConductorTripShiftUseCasesTest {

    @Mock
    lateinit var repo: RoomConductorTripShiftRepositoryInterface

    // some dummy domain models
    private val fromStation = StationDomain(code = 1, name = "A", city = "CityA")
    private val toStation = StationDomain(code = 2, name = "B", city = "CityB")
    private val trip = TripDomain(
        uuid = "uuid-123",
        trainNumber = "TN-01",
        departure = "2025-01-01T00:00:00Z",
        arrival = "2025-01-01T10:00:00Z",
        duration = "PT10H",
        from = fromStation,
        to = toStation
    )
    private val carriage = CarriageDomain(carNumber = "01", classType = "2Я")

    /**
     * GetAllShiftsUseCase должен просто делегировать repo.observeAllShifts()
     */
    @Test
    fun `GetAllShiftsUseCase returns flow from repository`() {
        val dummyShifts = listOf(
            ConductorTripShiftDomain(trip, carriage, TripShiftStatusDomain.ACTIVE)
        )
        whenever(repo.observeAllShifts()).thenReturn(flowOf(dummyShifts))

        val useCase = GetAllShiftsUseCase(repo)
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
        whenever(repo.observeActiveShift()).thenReturn(flowOf(active))

        val useCase = GetActiveShiftUseCase(repo)
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
        whenever(repo.getActiveShift()).thenReturn(
            ConductorTripShiftDomain(trip, null, TripShiftStatusDomain.ACTIVE)
        )
        val useCase = StartShiftUseCase(repo)
        val created = useCase(trip, carriage)
        assertFalse(created)
        verify(repo, never()).insertOrUpdate(any())
    }

    /**
     * StartShiftUseCase создаёт новую смену, если активной ещё нет
     */
    @Test
    fun `StartShiftUseCase returns true and inserts when no active shift`() = runTest {
        whenever(repo.getActiveShift()).thenReturn(null)

        val useCase = StartShiftUseCase(repo)
        val created = useCase(trip, carriage)
        assertTrue(created)

        val captor = argumentCaptor<ConductorTripShiftDomain>()
        verify(repo).insertOrUpdate(captor.capture())
        val inserted = captor.firstValue
        assertEquals(trip, inserted.trip)
        assertEquals(carriage, inserted.activeCarriage)
        assertEquals(TripShiftStatusDomain.ACTIVE, inserted.status)
    }

    /**
     * GenerateShiftReportUseCase должен сгенерировать JSON, пометить FINISHED и вернуть его
     */
    @Test
    fun `GenerateShiftReportUseCase generates report and updates status to FINISHED`() = runTest {
        val useCase = GenerateShiftReportUseCase(repo)
        val uuid = trip.uuid

        val reportJson = useCase(uuid)

        // проверим, что JSON содержит tripUuid
        assertTrue(reportJson.contains("\"tripUuid\":\"$uuid\""))

        // убедимся, что в репозиторий был вызов с правильными аргументами
        val statusCaptor = argumentCaptor<Int>()
        val reportCaptor = argumentCaptor<String>()
        val timeCaptor = argumentCaptor<Long>()
        verify(repo).updateStatusAndReport(
            eq(uuid),
            statusCaptor.capture(),
            reportCaptor.capture(),
            timeCaptor.capture()
        )
        assertEquals(TripShiftStatusDomain.FINISHED.ordinal, statusCaptor.firstValue)
        assertEquals(reportJson, reportCaptor.firstValue)
        // updatedAt должен совпадать с полем generatedAt из JSON или быть близким
        val tsInJson = Regex("\"generatedAt\":(\\d+)").find(reportJson)!!.groupValues[1].toLong()
        assertEquals(tsInJson, timeCaptor.firstValue)
    }

    /**
     * SendShiftReportUseCase должен пометить смену как SENT и вернуть true
     */
    @Test
    fun `SendShiftReportUseCase updates status to SENT and returns true`() = runTest {
        val useCase = SendShiftReportUseCase(repo)
        val uuid = trip.uuid

        val result = useCase(uuid)
        assertTrue(result)

        verify(repo).updateStatusAndReport(
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
        whenever(gen(uuid)).thenReturn("""{"tripUuid":"$uuid"}""")
        whenever(snd(uuid)).thenReturn(true)

        val useCase = CompleteShiftUseCase(gen, snd)
        val result = useCase(uuid)
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
        whenever(gen(uuid)).thenReturn("""{"tripUuid":"$uuid"}""")
        whenever(snd(uuid)).thenReturn(false)

        val useCase = CompleteShiftUseCase(gen, snd)
        val result = useCase(uuid)
        assertFalse(result)

        verify(gen).invoke(uuid)
        verify(snd).invoke(uuid)
    }
}
