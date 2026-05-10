package com.chaikasoft.app.data.room.sync

enum class SyncDataset(val key: String, val ttlMs: Long) {
    STATIONS(key = "stations", ttlMs = 90L * 24L * 60L * 60L * 1000L)
    // PRODUCTS(key = "products", ttlMs = 7L * 24L * 60L * 60L * 1000L) - for future
}
