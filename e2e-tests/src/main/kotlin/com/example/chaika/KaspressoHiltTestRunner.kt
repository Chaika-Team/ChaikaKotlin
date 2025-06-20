package com.example.chaika

import com.kaspersky.kaspresso.runner.KaspressoRunner
import dagger.hilt.android.testing.HiltTestApplication
import android.app.Application
import android.content.Context

class KaspressoHiltTestRunner : KaspressoRunner() {
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        // возвращаем HiltTestApplication, как у вас было
        return super.newApplication(
            cl,
            HiltTestApplication::class.java.name,
            context
        )
    }
}
