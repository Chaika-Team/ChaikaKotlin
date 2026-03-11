package com.chaikasoft.app.data.room.mappers

import com.chaikasoft.app.data.room.entities.Station
import com.chaikasoft.app.domain.models.trip.StationDomain
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class StationMapperTest : FunSpec({

    /**
     * Test design: equivalence classes.
     * Station mapper is pure 1:1 and should preserve all fields in both directions.
     */
    test("station maps both ways without data loss") {
        val entity = Station(
            code = "MSK",
            name = "Moscow",
            city = "Moscow",
        )
        val domain = StationDomain(
            code = "MSK",
            name = "Moscow",
            city = "Moscow",
        )

        entity.toDomain() shouldBe domain
        domain.toEntity() shouldBe entity
    }
})

