package com.chaikasoft.app.data.room.repo

import com.chaikasoft.app.data.room.dao.ConductorTripShiftDao
import com.chaikasoft.app.data.room.entities.ConductorTripShift
import com.chaikasoft.app.data.room.entities.Station
import com.chaikasoft.app.data.room.relations.ConductorTripShiftWithStations
import com.chaikasoft.app.domain.models.trip.ConductorTripShiftDomain
import com.chaikasoft.app.domain.models.trip.StationDomain
import com.chaikasoft.app.domain.models.trip.TripDomain
import com.chaikasoft.app.domain.models.trip.TripShiftStatusDomain
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest

class RoomConductorTripShiftRepositoryTest : FunSpec({

    lateinit var dao: ConductorTripShiftDao
    lateinit var repository: RoomConductorTripShiftRepository

    beforeTest {
        dao = mockk(relaxed = true)
        repository = RoomConductorTripShiftRepository(dao)
    }

    /**
     * Test design: decision table.
     * insertOrUpdate should choose update only for insert conflict (-1 rowId).
     */
    test("insertOrUpdate calls update only when insertIgnore returns -1") {
        runTest {
            val shift = sampleDomainShift("uuid-1")

            coEvery { dao.insertIgnore(any()) } returns -1L
            repository.insertOrUpdate(shift)
            coVerify(exactly = 1) { dao.update(any()) }

            coEvery { dao.insertIgnore(any()) } returns 1L
            repository.insertOrUpdate(shift)
            coVerify(exactly = 1) { dao.update(any()) }
        }
    }

    /**
     * Test design: boundary values.
     * Constraint conflict in tryStartNewShift must be converted to false.
     */
    test("tryStartNewShift returns false on SQLiteConstraintException and true on success") {
        runTest {
            val shift = sampleDomainShift("uuid-2")

            coEvery { dao.insertNew(any()) } throws android.database.sqlite.SQLiteConstraintException("conflict")
            repository.tryStartNewShift(shift) shouldBe false

            coEvery { dao.insertNew(any()) } returns Unit
            repository.tryStartNewShift(shift) shouldBe true
        }
    }

    /**
     * Test design: error guessing.
     * Missing shift by uuid is a hard invariant violation and must throw.
     */
    test("getStatusAndReport throws when shift is missing") {
        runTest {
            coEvery { dao.getByUuid("missing") } returns null

            shouldThrow<IllegalStateException> {
                repository.getStatusAndReport("missing")
            }
        }
    }

    /**
     * Test design: equivalence classes.
     * observeActiveShift should map relation model into domain model.
     */
    test("observeActiveShift maps dao flow to domain flow") {
        runTest {
            val relation = sampleRelationShift("uuid-3", TripShiftStatusDomain.ACTIVE)
            every {
                dao.getActiveShiftWithStationsFlow(TripShiftStatusDomain.ACTIVE.code)
            } returns MutableStateFlow(relation)

            val item = repository.observeActiveShift().first()

            item?.trip?.uuid shouldBe "uuid-3"
            item?.status shouldBe TripShiftStatusDomain.ACTIVE
        }
    }
}) {
    companion object {
        private fun sampleDomainShift(uuid: String) = ConductorTripShiftDomain(
            trip = TripDomain(
                uuid = uuid,
                trainNumber = "100A",
                departure = "2026-03-09T10:00:00Z",
                arrival = "2026-03-09T14:00:00Z",
                duration = "PT4H",
                from = StationDomain("MSK", "Moscow", "Moscow"),
                to = StationDomain("TVE", "Tver", "Tver"),
            ),
            activeCarriage = null,
            status = TripShiftStatusDomain.ACTIVE,
        )

        private fun sampleRelationShift(
            uuid: String,
            status: TripShiftStatusDomain,
        ) = ConductorTripShiftWithStations(
            shift = ConductorTripShift(
                uuid = uuid,
                trainNumber = "100A",
                departure = "2026-03-09T10:00:00Z",
                arrival = "2026-03-09T14:00:00Z",
                duration = "PT4H",
                fromCode = "MSK",
                toCode = "TVE",
                activeCarriage = null,
                status = status.code,
                report = null,
                createdAt = 1L,
                updatedAt = null,
            ),
            from = Station(code = "MSK", name = "Moscow", city = "Moscow"),
            to = Station(code = "TVE", name = "Tver", city = "Tver"),
        )
    }
}

