package com.example.chaika.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.chaika.data.room.dao.CartItemDao
import com.example.chaika.data.room.dao.CartOperationDao
import com.example.chaika.data.room.dao.ConductorDao
import com.example.chaika.data.room.dao.FastReportViewDao
import com.example.chaika.data.room.dao.PackageItemViewDao
import com.example.chaika.data.room.dao.ProductInfoDao
import com.example.chaika.data.room.entities.CartItem
import com.example.chaika.data.room.entities.CartOperation
import com.example.chaika.data.room.entities.Conductor
import com.example.chaika.data.room.entities.FastReportView
import com.example.chaika.data.room.entities.PackageItemView
import com.example.chaika.data.room.entities.ProductInfo

@Database(
    entities = [ProductInfo::class, Conductor::class, CartItem::class, CartOperation::class],
    views = [PackageItemView::class, FastReportView::class],
    version = 1, // Увеличить версию, если используем миграции
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productInfoDao(): ProductInfoDao
    abstract fun conductorDao(): ConductorDao
    abstract fun cartItemDao(): CartItemDao
    abstract fun cartOperationDao(): CartOperationDao
    abstract fun packageItemViewDao(): PackageItemViewDao
    abstract fun fastReportViewDao(): FastReportViewDao
}
