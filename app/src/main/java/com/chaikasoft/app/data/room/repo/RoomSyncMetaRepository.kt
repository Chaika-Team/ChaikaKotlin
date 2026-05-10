package com.chaikasoft.app.data.room.repo

import com.chaikasoft.app.data.room.dao.SyncMetaDao
import com.chaikasoft.app.data.room.entities.SyncMeta
import javax.inject.Inject

class RoomSyncMetaRepository @Inject constructor(private val dao: SyncMetaDao) :
    RoomSyncMetaRepositoryInterface {

    override suspend fun getLastSuccessfulSyncAt(datasetKey: String): Long? =
        dao.getLastSuccessfulSyncAt(datasetKey)

    override suspend fun setLastSuccessfulSyncAt(datasetKey: String, timestampMillis: Long) {
        dao.upsert(
            SyncMeta(
                datasetKey = datasetKey,
                lastSuccessfulSyncAt = timestampMillis
            )
        )
    }
}
