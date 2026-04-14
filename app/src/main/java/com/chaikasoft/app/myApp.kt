package com.chaikasoft.app

import android.app.Application
import android.util.Log
import com.chaikasoft.app.di.IoDispatcher
import com.chaikasoft.app.domain.sealed.RefreshStationsResult
import com.chaikasoft.app.domain.usecases.RefreshStationsOnLaunchUseCase
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@HiltAndroidApp
class MyApp : Application() {

    @Inject lateinit var refreshStationsOnLaunch: RefreshStationsOnLaunchUseCase

    @Inject @field:IoDispatcher
    lateinit var ioDispatcher: CoroutineDispatcher

    private val appScope by lazy(LazyThreadSafetyMode.NONE) {
        CoroutineScope(SupervisorJob() + ioDispatcher)
    }

    override fun onCreate() {
        super.onCreate()

        appScope.launch {
            try {
                when (val r = refreshStationsOnLaunch()) {
                    is RefreshStationsResult.SkippedActiveShift -> {
                        Log.i("MyApp", "Stations refresh skipped: active shift")
                    }

                    is RefreshStationsResult.Success -> {
                        Log.i("MyApp", "Stations refreshed: count=${r.stationCount}")
                    }

                    is RefreshStationsResult.RemoteFailure -> {
                        Log.w("MyApp", "Stations refresh failed (remote): ${r.error}")
                    }

                    is RefreshStationsResult.LocalFailure -> {
                        Log.e(
                            "MyApp",
                            "Stations refresh failed (local db): ${r.cause.message}",
                            r.cause
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // “Последняя сетка” — сюда не должны попадать сетевые ошибки, но баги возможны
                Log.e("MyApp", "Stations refresh crashed unexpectedly", e)
            }
        }
    }
}
