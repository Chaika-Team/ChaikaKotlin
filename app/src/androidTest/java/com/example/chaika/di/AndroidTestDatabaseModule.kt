package com.example.chaika.di

import android.content.Context
import androidx.room.Room
import com.example.chaika.data.room.AppDatabase
import com.example.chaika.data.room.dao.CartItemDao
import com.example.chaika.data.room.dao.CartOperationDao
import com.example.chaika.data.room.dao.ConductorDao
import com.example.chaika.data.room.dao.ConductorTripShiftDao
import com.example.chaika.data.room.dao.ProductInfoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class]
)
@Module
object AndroidTestDatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
        .allowMainThreadQueries() // Для тестов разрешаем запросы из main thread
        .build()

    @Provides
    fun provideCartItemDao(db: AppDatabase): CartItemDao = db.cartItemDao()

    @Provides
    fun provideCartOperationDao(db: AppDatabase): CartOperationDao = db.cartOperationDao()

    @Provides
    fun provideConductorDao(db: AppDatabase): ConductorDao = db.conductorDao()

    @Provides
    fun provideProductInfoDao(db: AppDatabase): ProductInfoDao = db.productInfoDao()

    @Provides
    fun provideConductorTripShiftDao(db: AppDatabase): ConductorTripShiftDao =
        db.conductorTripShiftDao()
}
