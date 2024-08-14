package com.example.chaika

import android.app.Application
import com.example.chaika.dataBase.AppDatabase
import com.example.chaika.dataBase.models.TripRepository
import com.example.chaika.dataBase.models.ProductRepository
import com.example.chaika.dataBase.models.ActionRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

@HiltAndroidApp
class MyApp : Application() {

    // Создаем CoroutineScope для жизненного цикла приложения.
    private val applicationScope = CoroutineScope(SupervisorJob())

    // Используем ленивую инициализацию для базы данных и репозиториев.
    val database by lazy { AppDatabase.getInstance(this, applicationScope) }
    val productRepository by lazy { ProductRepository(database.productDao()) }
    val tripRepository by lazy { TripRepository(database.tripDao()) }
    val actionRepository by lazy { ActionRepository(database.actionDao()) }

    override fun onCreate() {
        super.onCreate()
        // Можно вызывать getInstance здесь, чтобы инициализировать базу данных сразу при старте приложения.
        database
    }
}
