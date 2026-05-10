package com.chaikasoft.app.e2e.di

import android.content.Context
import androidx.room.Room
import com.chaikasoft.app.data.room.AppDatabase
import com.chaikasoft.app.data.room.dao.CartItemDao
import com.chaikasoft.app.data.room.dao.CartOperationDao
import com.chaikasoft.app.data.room.dao.ConductorDao
import com.chaikasoft.app.data.room.dao.ConductorTripShiftDao
import com.chaikasoft.app.data.room.dao.FastReportViewDao
import com.chaikasoft.app.data.room.dao.PackageItemViewDao
import com.chaikasoft.app.data.room.dao.ProductInfoDao
import com.chaikasoft.app.data.room.dao.StationDao
import com.chaikasoft.app.data.room.dao.SyncMetaDao
import com.chaikasoft.app.di.DatabaseModule
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class],
)
object E2EDatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
        .allowMainThreadQueries()
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
    fun provideConductorTripShiftDao(db: AppDatabase): ConductorTripShiftDao = db.conductorTripShiftDao()

    @Provides
    fun provideStationDao(db: AppDatabase): StationDao = db.stationDao()

    @Provides
    fun provideSyncMetaDao(db: AppDatabase): SyncMetaDao = db.syncMetaDao()

    @Provides
    fun providePackageItemViewDao(db: AppDatabase): PackageItemViewDao = db.packageItemViewDao()

    @Provides
    fun provideFastReportViewDao(db: AppDatabase): FastReportViewDao = db.fastReportViewDao()
}
