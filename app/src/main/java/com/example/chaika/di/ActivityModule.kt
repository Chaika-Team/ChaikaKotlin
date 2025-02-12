package com.example.chaika.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
object ActivityModule {
    // В этом модуле можно определять зависимости, специфичные для Activity,
    // например, объекты, требующие ActivityContext.
    // В настоящий момент все зависимости, связанные с OAuth, предоставляются в AppModule.
}
