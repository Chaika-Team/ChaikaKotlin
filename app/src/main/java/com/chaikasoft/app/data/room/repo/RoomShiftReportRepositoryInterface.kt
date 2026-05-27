package com.chaikasoft.app.data.room.repo

/**
 * Room repository for atomically finishing a shift and persisting its generated report.
 */
interface RoomShiftReportRepositoryInterface {
    /**
     * Builds a snapshot report, stores it in `conductor_trip_shifts.report`, marks the shift
     * FINISHED, and clears cart operations in one Room transaction.
     *
     * @return the exact JSON persisted in the shift row.
     */
    suspend fun finishShiftWithReport(uuid: String): String
}
