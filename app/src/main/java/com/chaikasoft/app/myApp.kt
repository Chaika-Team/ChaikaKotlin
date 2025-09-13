package com.chaikasoft.app

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import com.chaikasoft.app.domain.usecases.RefreshStationsOnLaunchUseCase


@HiltAndroidApp
class MyApp : Application() {
    @Inject lateinit var refreshStationsOnLaunch: RefreshStationsOnLaunchUseCase
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        appScope.launch {
            try {
                refreshStationsOnLaunch()
            } catch (t: Throwable) {
                Log.w("MyApp", "Stations refresh skipped/failed", t)
            }
        }
    }
}