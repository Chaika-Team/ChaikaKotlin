package com.example.chaika.data.room.dao

import androidx.room.*
import com.example.chaika.data.room.entities.ConductorTripShift
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
    @Query("SELECT * FROM conductor_trip_shifts WHERE status = :status")
    fun getByStatus(status: Int): Flow<List<ConductorTripShift>>

    /** Flow для одной активной смены */
    @Query("SELECT * FROM conductor_trip_shifts WHERE status = :activeStatus LIMIT 1")
    fun getActiveShiftFlow(activeStatus: Int = 0): Flow<ConductorTripShift?>  // <- nullable

    /** Flow для всех смен */
    @Query("SELECT * FROM conductor_trip_shifts")
    fun getAll(): Flow<List<ConductorTripShift>>

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
        reportJson: String?,    // передаём null, если report трогать не надо
        updatedAt: Long
    )


    /**
     * Удобный метод, чтобы достать текущую (active) смену проводника, если она есть.
     */
    @Query("SELECT * FROM conductor_trip_shifts WHERE status = :activeStatus LIMIT 1")
    suspend fun getActiveShift(activeStatus: Int = 0 /* TripShiftStatusDomain.ACTIVE.ordinal */): ConductorTripShift?
}
