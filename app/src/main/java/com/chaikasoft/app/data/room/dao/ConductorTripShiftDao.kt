package com.chaikasoft.app.data.room.dao

import androidx.room.*
import com.chaikasoft.app.data.room.entities.ConductorTripShift
import com.chaikasoft.app.data.room.relations.ConductorTripShiftWithStations
import kotlinx.coroutines.flow.Flow

@Dao
interface ConductorTripShiftDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(shift: ConductorTripShift)

    @Update
    suspend fun update(shift: ConductorTripShift)

    @Query("SELECT * FROM conductor_trip_shifts WHERE uuid = :uuid")
    suspend fun getByUuid(uuid: String): ConductorTripShift?

    /** Вместо String — Int, потому что status в сущности хранится как Int */
    @Transaction
    @Query("SELECT * FROM conductor_trip_shifts WHERE status = :status")
    fun getByStatusWithStations(status: Int): Flow<List<ConductorTripShiftWithStations>>

    /** Flow для одной активной смены */
    @Transaction
    @Query("SELECT * FROM conductor_trip_shifts WHERE status = :activeStatus LIMIT 1")
    fun getActiveShiftWithStationsFlow(activeStatus: Int = 0): Flow<ConductorTripShiftWithStations?>


    /** Flow для всех смен */
    @Transaction
    @Query("SELECT * FROM conductor_trip_shifts")
    fun getAllWithStations(): Flow<List<ConductorTripShiftWithStations>>

    @Query("DELETE FROM conductor_trip_shifts WHERE uuid = :uuid")
    suspend fun deleteByUuid(uuid: String)

    @Query("DELETE FROM conductor_trip_shifts")
    suspend fun clearAll()

    /**
     * Обновляет статус, при этом:
     *  - если передан reportJson ≠ null — обновляет и поле report,
     *  - если reportJson == null — оставляет report как есть.
     */
    @Query("""
        UPDATE conductor_trip_shifts 
           SET status    = :newStatus,
               report    = COALESCE(:reportJson, report),
               updatedAt = :updatedAt
         WHERE uuid = :uuid
    """)
    suspend fun updateStatusAndReport(
        uuid: String,
        newStatus: Int,
        reportJson: String?,
        updatedAt: Long
    )

    @Transaction
    @Query("SELECT * FROM conductor_trip_shifts WHERE status = :activeStatus LIMIT 1")
    suspend fun getActiveShiftWithStations(activeStatus: Int = 0): ConductorTripShiftWithStations?
}
