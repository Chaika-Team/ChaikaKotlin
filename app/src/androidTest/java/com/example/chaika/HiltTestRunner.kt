package com.example.chaika

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

class HiltTestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        // Возвращаем HiltTestApplication вместо вашего основного приложения
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}
