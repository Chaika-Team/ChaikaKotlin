// package скорректируйте под ваш проект
package com.chaikasoft.app.ui.helpers

import com.chaikasoft.app.domain.models.trip.CarriageDomain
import com.chaikasoft.app.domain.models.trip.StationDomain
import com.chaikasoft.app.domain.models.trip.TripDomain
import com.chaikasoft.app.util.durationHms
import com.chaikasoft.app.util.generateLocalUuid
import com.chaikasoft.app.util.localToRfc3339Utc
import com.chaikasoft.app.util.normalizeForDisplay
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Строит TripDomain/CarriageDomain из выбранных станций и времени.
 * Без генерации каких-либо кодов станций — они уже валидные из БД.
 */
object OfflineTripBuildHelper {

    data class Input(
        val trainNumber: String,
        val fromStation: StationDomain?,  // выбрано из БД
        val toStation: StationDomain?,    // выбрано из БД
        val departure: LocalDateTime?,
        val arrival: LocalDateTime?,
        val carriageNumber: String,
        val carriageClassType: String?
    )

    sealed interface BuildError {
        data object TrainNumberEmpty : BuildError
        data object FromStationMissing : BuildError
        data object ToStationMissing : BuildError
        data object DepartureMissing : BuildError
        data object ArrivalMissing : BuildError
        data object CarriageNumberEmpty : BuildError
        data object SameStations : BuildError
        data object ArrivalNotAfterDeparture : BuildError
    }

    data class Output(val trip: TripDomain, val carriage: CarriageDomain)

    sealed interface BuildResult {
        data class Success(val output: Output) : BuildResult
        data class Invalid(val errors: List<BuildError>) : BuildResult
        data class Failure(val cause: Throwable) : BuildResult
    }

    fun isValid(input: Input): Boolean = validate(input).isEmpty()

    fun build(
        input: Input,
        zone: ZoneId = ZoneId.systemDefault(),
        defaultClassType: String
    ): BuildResult {
        val errors = validate(input)
        if (errors.isNotEmpty()) return BuildResult.Invalid(errors)

        return try {
            // здесь уже ок вызывать requireNotNull (или оставить !!, как у тебя)
            val dep  = requireNotNull(input.departure)
            val arr  = requireNotNull(input.arrival)
            val from = requireNotNull(input.fromStation)
            val to   = requireNotNull(input.toStation)

            val trip = TripDomain(
                uuid        = generateLocalUuid(),
                trainNumber = normalizeForDisplay(input.trainNumber),
                departure   = localToRfc3339Utc(dep, zone),
                arrival     = localToRfc3339Utc(arr, zone),
                duration    = durationHms(dep, arr, zone),
                from        = from,
                to          = to
            )
            val carriage = CarriageDomain(
                carNumber = normalizeForDisplay(input.carriageNumber),
                classType = input.carriageClassType?.ifBlank { defaultClassType } ?: defaultClassType
            )
            BuildResult.Success(Output(trip, carriage))
        } catch (t: IllegalStateException) {
            BuildResult.Failure(t)
        }
    }

    private fun validate(i: Input): List<BuildError> {
        val errs = mutableListOf<BuildError>()

        if (i.trainNumber.isBlank())     errs += BuildError.TrainNumberEmpty
        if (i.fromStation == null)       errs += BuildError.FromStationMissing
        if (i.toStation == null)         errs += BuildError.ToStationMissing
        if (i.departure == null)         errs += BuildError.DepartureMissing
        if (i.arrival == null)           errs += BuildError.ArrivalMissing
        if (i.carriageNumber.isBlank())  errs += BuildError.CarriageNumberEmpty

        if (errs.isEmpty()) {
            val from = i.fromStation
            val to   = i.toStation
            val dep  = i.departure
            val arr  = i.arrival

            if (from != null && to != null && from.code == to.code)
                errs += BuildError.SameStations

            if (dep != null && arr != null && !arr.isAfter(dep))
                errs += BuildError.ArrivalNotAfterDeparture
        }
        return errs
    }
}
