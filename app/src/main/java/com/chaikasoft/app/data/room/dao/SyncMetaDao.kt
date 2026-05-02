package com.chaikasoft.app.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.chaikasoft.app.data.room.entities.SyncMeta

@Dao
interface SyncMetaDao {

    @Query("SELECT lastSuccessfulSyncAt FROM sync_meta WHERE datasetKey = :datasetKey LIMIT 1")
    suspend fun getLastSuccessfulSyncAt(datasetKey: String): Long?

    @Upsert
    suspend fun upsert(meta: SyncMeta)
}
