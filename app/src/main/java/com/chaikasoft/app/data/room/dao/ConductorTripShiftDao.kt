package com.chaikasoft.app.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.chaikasoft.app.data.room.entities.ConductorTripShift
import com.chaikasoft.app.data.room.relations.ConductorTripShiftWithStations
import kotlinx.coroutines.flow.Flow

@Dao
interface ConductorTripShiftDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertNew(shift: ConductorTripShift)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIgnore(shift: ConductorTripShift): Long

    @Update
    suspend fun update(shift: ConductorTripShift)

    @Query("SELECT * FROM conductor_trip_shifts WHERE uuid = :uuid")
    suspend fun getByUuid(uuid: String): ConductorTripShift?

    @Transaction
    @Query("SELECT * FROM conductor_trip_shifts WHERE uuid = :uuid LIMIT 1")
    suspend fun getByUuidWithStations(uuid: String): ConductorTripShiftWithStations?

    @Transaction
    @Query("SELECT * FROM conductor_trip_shifts WHERE status = :status")
    fun getByStatusWithStations(status: Int): Flow<List<ConductorTripShiftWithStations>>

    /** Flow для одной активной смены */
    @Transaction
    @Query(
        """
        SELECT * FROM conductor_trip_shifts
        WHERE status = :activeStatus
        ORDER BY COALESCE(updatedAt, createdAt) DESC
        LIMIT 1
        """
    )
    fun getActiveShiftWithStationsFlow(activeStatus: Int): Flow<ConductorTripShiftWithStations?>

    /** Flow для всех смен */
    @Transaction
    @Query("SELECT * FROM conductor_trip_shifts")
    fun getAllWithStations(): Flow<List<ConductorTripShiftWithStations>>

    @Transaction
    @Query(
        """
        SELECT * FROM conductor_trip_shifts
        WHERE status != :activeStatus
        ORDER BY COALESCE(updatedAt, createdAt) DESC
        """
    )
    fun getHistoryWithStations(activeStatus: Int = 0): Flow<List<ConductorTripShiftWithStations>>

    @Query("DELETE FROM conductor_trip_shifts WHERE uuid = :uuid")
    suspend fun deleteByUuid(uuid: String)

    @Query("DELETE FROM conductor_trip_shifts")
    suspend fun clearAll()

    /**
     * Обновляет статус, при этом:
     *  - если передан reportJson ≠ null — обновляет и поле report,
     *  - если reportJson == null — оставляет report как есть.
     */
    @Query(
        """
        UPDATE conductor_trip_shifts
        SET status    = :newStatus,
            report    = COALESCE(:reportJson, report),
            updatedAt = :updatedAt
        WHERE uuid = :uuid
        """
    )
    suspend fun updateStatusAndReport(
        uuid: String,
        newStatus: Int,
        reportJson: String?,
        updatedAt: Long
    )

    @Transaction
    @Query(
        """
        SELECT * FROM conductor_trip_shifts
        WHERE status = :activeStatus
        ORDER BY COALESCE(updatedAt, createdAt) DESC
        LIMIT 1
        """
    )
    suspend fun getActiveShiftWithStations(activeStatus: Int): ConductorTripShiftWithStations?
}
