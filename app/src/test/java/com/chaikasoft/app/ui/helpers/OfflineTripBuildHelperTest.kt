package com.chaikasoft.app.ui.helpers

import com.chaikasoft.app.ui.station
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime
import java.time.ZoneId

class OfflineTripBuildHelperTest : FunSpec({

    fun validInput() = OfflineTripBuildHelper.Input(
        trainNumber = " 123A ",
        fromStation = station(code = "100", name = "From"),
        toStation = station(code = "200", name = "To"),
        departure = LocalDateTime.of(2026, 1, 1, 10, 0),
        arrival = LocalDateTime.of(2026, 1, 1, 12, 0),
        carriageNumber = " 07 ",
        carriageClassType = " "
    )

    test("isValid returns false when required fields are empty") {
        val invalid = OfflineTripBuildHelper.Input(
            trainNumber = "",
            fromStation = null,
            toStation = null,
            departure = null,
            arrival = null,
            carriageNumber = "",
            carriageClassType = null
        )

        OfflineTripBuildHelper.isValid(invalid).shouldBeFalse()
        when (val result = OfflineTripBuildHelper.build(invalid, defaultClassType = "Default")) {
            is OfflineTripBuildHelper.BuildResult.Invalid -> {
                result.errors shouldContainAll listOf(
                    OfflineTripBuildHelper.BuildError.TrainNumberEmpty,
                    OfflineTripBuildHelper.BuildError.FromStationMissing,
                    OfflineTripBuildHelper.BuildError.ToStationMissing,
                    OfflineTripBuildHelper.BuildError.DepartureMissing,
                    OfflineTripBuildHelper.BuildError.ArrivalMissing,
                    OfflineTripBuildHelper.BuildError.CarriageNumberEmpty
                )
            }

            else -> error("Expected Invalid result")
        }
    }

    test("build returns Invalid for same stations and non-increasing time") {
        val input = validInput().copy(
            toStation = station(code = "100", name = "Same"),
            arrival = LocalDateTime.of(2026, 1, 1, 10, 0)
        )

        when (val result = OfflineTripBuildHelper.build(input, defaultClassType = "Default")) {
            is OfflineTripBuildHelper.BuildResult.Invalid -> {
                result.errors shouldHaveSize 2
                result.errors shouldContain OfflineTripBuildHelper.BuildError.SameStations
                result.errors shouldContain OfflineTripBuildHelper.BuildError.ArrivalNotAfterDeparture
            }

            else -> error("Expected Invalid result")
        }
    }

    test("build returns Success with normalized values and default class type") {
        val input = validInput()

        OfflineTripBuildHelper.isValid(input).shouldBeTrue()
        when (
            val result = OfflineTripBuildHelper.build(
                input = input,
                zone = ZoneId.of("Europe/Moscow"),
                defaultClassType = "DefaultClass"
            )
        ) {
            is OfflineTripBuildHelper.BuildResult.Success -> {
                result.output.trip.trainNumber shouldBe "123A"
                result.output.carriage.carNumber shouldBe "07"
                result.output.carriage.classType shouldBe "DefaultClass"
            }

            else -> error("Expected Success result")
        }
    }
})

