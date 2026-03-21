package com.chaikasoft.app.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.chaikasoft.app.data.room.AppDatabase
import com.chaikasoft.app.data.room.dao.CartItemDao
import com.chaikasoft.app.data.room.dao.CartOperationDao
import com.chaikasoft.app.data.room.dao.ConductorDao
import com.chaikasoft.app.data.room.dao.ConductorTripShiftDao
import com.chaikasoft.app.data.room.dao.FastReportViewDao
import com.chaikasoft.app.data.room.dao.PackageItemViewDao
import com.chaikasoft.app.data.room.dao.ProductInfoDao
import com.chaikasoft.app.data.room.dao.StationDao
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

    private const val SHIFT_STATUS_ACTIVE = 0

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase = Room.databaseBuilder(context, AppDatabase::class.java, "app_database")
        .fallbackToDestructiveMigration()   // мы «переустанавливаем приложение», миграции не пишем
        .setQueryCallback({ sql, args -> Log.d("ROOM-SQL-CHAIKA", "$sql -- $args") }, Executors.newSingleThreadExecutor())
        .addCallback(object : RoomDatabase.Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                try {
                    db.execSQL("""
            CREATE UNIQUE INDEX IF NOT EXISTS idx_unique_active_shift
            ON conductor_trip_shifts(status)
            WHERE status = $SHIFT_STATUS_ACTIVE
        """.trimIndent())
                } catch (e: android.database.sqlite.SQLiteException) {
                    Log.e("ROOM", "Failed to ensure idx_unique_active_shift", e)
                    throw e
                }
            }
        })
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
    fun provideStationDao(db: AppDatabase): StationDao = db.stationDao()

    @Provides
    fun providePackageItemViewDao(db: AppDatabase): PackageItemViewDao = db.packageItemViewDao()

    @Provides
    fun provideFastReportViewDao(db: AppDatabase): FastReportViewDao = db.fastReportViewDao()
}
