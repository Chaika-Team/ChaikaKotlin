package com.chaikasoft.app.di

import android.content.Context
import com.chaikasoft.app.data.local.LocalImageRepository
import com.chaikasoft.app.data.local.LocalImageRepositoryInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object FileRepositoriesModule {

    @Provides
    @Singleton
    fun provideLocalImageRepository(
        @ApplicationContext context: Context,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): LocalImageRepositoryInterface =
        LocalImageRepository(context = context, ioDispatcher = ioDispatcher)
}
