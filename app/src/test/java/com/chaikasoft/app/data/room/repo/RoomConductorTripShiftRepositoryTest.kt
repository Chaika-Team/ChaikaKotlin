package com.chaikasoft.app.data.room.repo

import com.chaikasoft.app.data.room.dao.ConductorTripShiftDao
import com.chaikasoft.app.data.room.entities.ConductorTripShift
import com.chaikasoft.app.data.room.entities.Station
import com.chaikasoft.app.data.room.relations.ConductorTripShiftWithStations
import com.chaikasoft.app.domain.models.trip.TripShiftStatusDomain
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
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
        repository = RoomConductorTripShiftRepository(
            db = mockk(relaxed = true),
            dao = dao,
            cartOperationDao = mockk(relaxed = true)
        )
    }

    test("getStatusAndReport throws when shift is missing") {
        runTest {
            coEvery { dao.getByUuid("missing") } returns null

            shouldThrow<IllegalStateException> {
                repository.getStatusAndReport("missing")
            }
        }
    }

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
