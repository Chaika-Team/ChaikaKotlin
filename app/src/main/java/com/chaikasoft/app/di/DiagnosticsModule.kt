package com.chaikasoft.app.di

import com.chaikasoft.app.data.diagnostics.LogcatErrorReporter
import com.chaikasoft.app.domain.common.ErrorReporter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DiagnosticsModule {
    @Binds
    @Singleton
    fun bindErrorReporter(reporter: LogcatErrorReporter): ErrorReporter
}
