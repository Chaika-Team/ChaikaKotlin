package com.chaikasoft.app.data.room.repo

interface RoomSyncMetaRepositoryInterface {
    suspend fun getLastSuccessfulSyncAt(datasetKey: String): Long?
    suspend fun setLastSuccessfulSyncAt(datasetKey: String, timestampMillis: Long)
}
