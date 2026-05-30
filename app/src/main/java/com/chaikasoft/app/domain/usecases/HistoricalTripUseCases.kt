package com.chaikasoft.app.domain.usecases

import com.chaikasoft.app.data.room.repo.RoomConductorRepositoryInterface
import com.chaikasoft.app.data.room.repo.RoomProductInfoRepositoryInterface
import com.chaikasoft.app.domain.mappers.toHistoricalTripSnapshot
import com.chaikasoft.app.domain.models.ConductorDomain
import com.chaikasoft.app.domain.models.HistoricalTripSnapshot
import com.chaikasoft.app.domain.models.report.ShiftReportReport
import com.chaikasoft.app.domain.models.trip.TripShiftStatusDomain
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import java.io.IOException
import javax.inject.Inject

private const val UNKNOWN_CONDUCTOR_GIVEN_NAME = ""
private const val UNKNOWN_CONDUCTOR_IMAGE = ""

class HistoricalTripSnapshotException(message: String, cause: Throwable? = null) :
    IllegalStateException(message, cause)

/**
 * Restores a read-only historical trip snapshot from the saved shift report JSON.
 *
 * The use case never reads live cart operation tables. It parses the persisted report and resolves
 * only display metadata, such as product and conductor names, from the current local database.
 */
class GetHistoricalTripSnapshotUseCase @Inject constructor(
    private val getShiftReportJson: GetShiftReportJsonUseCase,
    private val productRepository: RoomProductInfoRepositoryInterface,
    private val conductorRepository: RoomConductorRepositoryInterface,
    moshi: Moshi
) {
    private val adapter = moshi.adapter(ShiftReportReport::class.java)

    suspend operator fun invoke(shiftUuid: String): HistoricalTripSnapshot {
        val (status, reportJson) = getShiftReportJson(shiftUuid)
        if (status != TripShiftStatusDomain.FINISHED && status != TripShiftStatusDomain.SENT) {
            throw HistoricalTripSnapshotException(
                "Historical report is unavailable for shift status $status"
            )
        }

        val json = reportJson?.takeIf { it.isNotBlank() }
            ?: throw HistoricalTripSnapshotException("Historical report is missing")
        val report = parseReport(json)
        val productsById = productRepository.getAllProductsOnce().associateBy { it.id }
        val conductorsByEmployeeId = mutableMapOf<String, ConductorDomain>()

        return report.toHistoricalTripSnapshot(
            productsById = productsById,
            resolveConductor = { employeeId ->
                resolveConductor(
                    employeeId = employeeId,
                    conductorsByEmployeeId = conductorsByEmployeeId
                )
            }
        )
    }

    private fun parseReport(json: String): ShiftReportReport {
        val report = try {
            adapter.fromJson(json)
        } catch (error: IOException) {
            throw HistoricalTripSnapshotException("Historical report is malformed", error)
        } catch (error: JsonDataException) {
            throw HistoricalTripSnapshotException("Historical report is malformed", error)
        }

        return report ?: throw HistoricalTripSnapshotException("Historical report is empty")
    }

    private suspend fun resolveConductor(
        employeeId: String,
        conductorsByEmployeeId: MutableMap<String, ConductorDomain>
    ): ConductorDomain {
        val cached = conductorsByEmployeeId[employeeId]
        if (cached != null) return cached

        val conductor = conductorRepository.getConductorByEmployeeID(employeeId)
            ?: fallbackConductor(employeeId)
        conductorsByEmployeeId[employeeId] = conductor
        return conductor
    }

    private fun fallbackConductor(employeeId: String): ConductorDomain = ConductorDomain(
        name = "",
        familyName = "",
        givenName = UNKNOWN_CONDUCTOR_GIVEN_NAME,
        employeeID = employeeId,
        image = UNKNOWN_CONDUCTOR_IMAGE
    )
}
