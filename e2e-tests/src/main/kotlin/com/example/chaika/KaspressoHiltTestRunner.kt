package com.chaikasoft.app

import android.app.Application
import android.content.Context
import com.kaspersky.kaspresso.runner.KaspressoRunner
import dagger.hilt.android.testing.HiltTestApplication

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
