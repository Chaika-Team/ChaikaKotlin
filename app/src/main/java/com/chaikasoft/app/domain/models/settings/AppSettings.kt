package com.chaikasoft.app.domain.models.settings

data class AppSettings(
    val notificationsEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val darkThemeEnabled: Boolean = false,
    val autoSyncEnabled: Boolean = true
)
