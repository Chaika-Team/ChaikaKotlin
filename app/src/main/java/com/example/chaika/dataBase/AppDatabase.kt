package com.example.chaika.dataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.chaika.dataBase.dao.ActionDao
import com.example.chaika.dataBase.dao.OperationDao
import com.example.chaika.dataBase.dao.ProductDao
import com.example.chaika.dataBase.dao.TripDao
import com.example.chaika.dataBase.entities.Action
import com.example.chaika.dataBase.entities.Operation
import com.example.chaika.dataBase.entities.Product
import com.example.chaika.dataBase.entities.Trip

// Аннотация Database для Room, перечислите все сущности и установите версию базы данных.
@Database(entities = [Product::class, Operation::class, Action::class, Trip::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    // Абстрактные методы для получения DAO.
    abstract fun productDao(): ProductDao
    abstract fun operationDao(): OperationDao
    abstract fun actionDao(): ActionDao
    abstract fun tripDao(): TripDao

    companion object {
        // Волатильная переменная для инстанса базы данных, чтобы гарантировать единственный экземпляр.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Метод для получения инстанса базы данных.
        fun getInstance(context: Context): AppDatabase {
            // Если инстанс уже существует, возвращаем его, если нет - создаем новый.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "chaika_database" // Имя файла базы данных
                )
                    .fallbackToDestructiveMigration() // Стратегия миграции при изменении схемы базы данных.
                    .build()
                INSTANCE = instance
                // Возвращаем новый или существующий инстанс базы данных.
                instance
            }
        }
    }
}
