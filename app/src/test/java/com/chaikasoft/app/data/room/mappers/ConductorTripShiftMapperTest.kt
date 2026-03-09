package com.chaikasoft.app.data.room.mappers

import com.chaikasoft.app.data.room.entities.ConductorTripShift
import com.chaikasoft.app.data.room.entities.Station
import com.chaikasoft.app.data.room.relations.ConductorTripShiftWithStations
import com.chaikasoft.app.domain.models.trip.CarriageDomain
import com.chaikasoft.app.domain.models.trip.ConductorTripShiftDomain
import com.chaikasoft.app.domain.models.trip.StationDomain
import com.chaikasoft.app.domain.models.trip.TripDomain
import com.chaikasoft.app.domain.models.trip.TripShiftStatusDomain
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.shouldBe

class ConductorTripShiftMapperTest : FunSpec({

    /**
     * Test design: equivalence classes.
     * Status code conversion must remain stable in both directions.
     */
    test("status converts both ways and throws for unknown code") {
        TripShiftStatusDomain.entries.forEach { status ->
            status.code.toTripShiftStatusDomain() shouldBe status
            status.toInt() shouldBe status.code
        }

        shouldThrow<IllegalArgumentException> {
            (-1).toTripShiftStatusDomain()
        }
    }

    /**
     * Test design: state transition mapping.
     * Domain to entity must serialize station codes and initialize report metadata.
     */
    test("toEntity maps codes and initializes report fields") {
        val domain = ConductorTripShiftDomain(
            trip = TripDomain(
                uuid = "uuid-1",
                trainNumber = "100A",
                departure = "2026-03-10T10:00:00Z",
                arrival = "2026-03-10T15:00:00Z",
                duration = "PT5H",
                from = StationDomain("MSK", "Moscow", "Moscow"),
                to = StationDomain("SPB", "Saint Petersburg", "Saint Petersburg"),
            ),
            activeCarriage = CarriageDomain(carNumber = "05", classType = "cupe"),
            status = TripShiftStatusDomain.ACTIVE,
        )

        val entity = domain.toEntity()

        entity.uuid shouldBe "uuid-1"
        entity.fromCode shouldBe "MSK"
        entity.toCode shouldBe "SPB"
        entity.status shouldBe TripShiftStatusDomain.ACTIVE.code
        entity.report shouldBe null
        entity.updatedAt shouldBe null
        entity.createdAt shouldBeGreaterThan 0L
    }

    /**
     * Test design: equivalence classes.
     * Relation to domain must map all nested trip fields and status.
     */
    test("toDomain maps relation with stations to domain model") {
        val relation = ConductorTripShiftWithStations(
            shift = ConductorTripShift(
                uuid = "uuid-2",
                trainNumber = "200B",
                departure = "2026-03-10T06:00:00Z",
                arrival = "2026-03-10T09:00:00Z",
                duration = "PT3H",
                fromCode = "TVE",
                toCode = "MSK",
                activeCarriage = null,
                status = TripShiftStatusDomain.FINISHED.code,
                report = """{"ok":true}""",
                createdAt = 1L,
                updatedAt = 2L,
            ),
            from = Station(code = "TVE", name = "Tver", city = "Tver"),
            to = Station(code = "MSK", name = "Moscow", city = "Moscow"),
        )

        val domain = relation.toDomain()

        domain.trip.uuid shouldBe "uuid-2"
        domain.trip.from shouldBe StationDomain("TVE", "Tver", "Tver")
        domain.trip.to shouldBe StationDomain("MSK", "Moscow", "Moscow")
        domain.status shouldBe TripShiftStatusDomain.FINISHED
    }
})

