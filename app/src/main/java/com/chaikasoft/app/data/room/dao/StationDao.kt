package com.chaikasoft.app.data.room.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.chaikasoft.app.data.room.entities.Station

@Dao
interface StationDao {
    // Рассмотреть @Insert(onConflict = OnConflictStrategy.IGNORE) вместо @Upsert
    @Upsert suspend fun upsertAll(stations: List<Station>)

    @Query("SELECT EXISTS(SELECT 1 FROM stations LIMIT 1)")
    suspend fun hasAnyStationsOnce(): Boolean

    @Query(
        """
        SELECT * FROM stations
        WHERE name COLLATE NOCASE LIKE :pattern ESCAPE '\'
        ORDER BY name ASC, city ASC
        """
    )
    fun pagingByQuery(pattern: String): PagingSource<Int, Station>

    @Query("SELECT * FROM stations WHERE code=:code LIMIT 1")
    suspend fun getByCode(code: String): Station?
}
