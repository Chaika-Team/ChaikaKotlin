package com.example.chaika.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.example.chaika.data.room.AppDatabase
import com.example.chaika.data.room.dao.CartItemDao
import com.example.chaika.data.room.dao.CartOperationDao
import com.example.chaika.data.room.dao.ConductorDao
import com.example.chaika.data.room.dao.ConductorTripShiftDao
import com.example.chaika.data.room.dao.FastReportViewDao
import com.example.chaika.data.room.dao.PackageItemViewDao
import com.example.chaika.data.room.dao.ProductInfoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase = Room.databaseBuilder(context, AppDatabase::class.java, "app_database")
        .setQueryCallback({ sql, args -> Log.d("ROOM-SQL-CHAIKA", "$sql -- $args") }, Executors.newSingleThreadExecutor())
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

    @Provides
    fun providePackageItemViewDao(db: AppDatabase): PackageItemViewDao = db.packageItemViewDao()

    @Provides
    fun provideFastReportViewDao(db: AppDatabase): FastReportViewDao = db.fastReportViewDao()
}
