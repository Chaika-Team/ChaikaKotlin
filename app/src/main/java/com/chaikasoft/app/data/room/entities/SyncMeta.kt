package com.chaikasoft.app.data.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_meta")
data class SyncMeta(@PrimaryKey val datasetKey: String, val lastSuccessfulSyncAt: Long)
