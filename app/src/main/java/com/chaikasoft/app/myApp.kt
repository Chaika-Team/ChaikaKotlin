package com.chaikasoft.app

import android.app.Application
import com.chaikasoft.app.diagnostics.SentryDiagnosticsInitializer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        SentryDiagnosticsInitializer.init(this)
    }
}
