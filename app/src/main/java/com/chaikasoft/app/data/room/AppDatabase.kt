package com.chaikasoft.app.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.chaikasoft.app.data.room.dao.CartItemDao
import com.chaikasoft.app.data.room.dao.CartOperationDao
import com.chaikasoft.app.data.room.dao.ConductorDao
import com.chaikasoft.app.data.room.dao.ConductorTripShiftDao
import com.chaikasoft.app.data.room.dao.FastReportViewDao
import com.chaikasoft.app.data.room.dao.PackageItemViewDao
import com.chaikasoft.app.data.room.dao.ProductInfoDao
import com.chaikasoft.app.data.room.dao.StationDao
import com.chaikasoft.app.data.room.dao.SyncMetaDao
import com.chaikasoft.app.data.room.entities.CartItem
import com.chaikasoft.app.data.room.entities.CartOperation
import com.chaikasoft.app.data.room.entities.Conductor
import com.chaikasoft.app.data.room.entities.ConductorTripShift
import com.chaikasoft.app.data.room.entities.FastReportView
import com.chaikasoft.app.data.room.entities.OperationInfoView
import com.chaikasoft.app.data.room.entities.PackageItemView
import com.chaikasoft.app.data.room.entities.ProductInfo
import com.chaikasoft.app.data.room.entities.Station
import com.chaikasoft.app.data.room.entities.SyncMeta

@Database(
    entities = [
        ProductInfo::class,
        Conductor::class,
        CartItem::class,
        CartOperation::class,
        ConductorTripShift::class,
        Station::class,
        SyncMeta::class
    ],
    views = [PackageItemView::class, FastReportView::class, OperationInfoView::class],
    version = 1, // Увеличить версию, если используем миграции
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productInfoDao(): ProductInfoDao
    abstract fun conductorDao(): ConductorDao
    abstract fun cartItemDao(): CartItemDao
    abstract fun cartOperationDao(): CartOperationDao
    abstract fun conductorTripShiftDao(): ConductorTripShiftDao
    abstract fun stationDao(): StationDao
    abstract fun syncMetaDao(): SyncMetaDao
    abstract fun packageItemViewDao(): PackageItemViewDao
    abstract fun fastReportViewDao(): FastReportViewDao
}
