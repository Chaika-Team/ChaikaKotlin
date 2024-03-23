package com.example.chaika

import android.app.Application
import com.example.chaika.dataBase.AppDatabase
import com.example.chaika.activities.mainActivity.TripRepository

class MyApp : Application() {
    lateinit var database: AppDatabase
    val repository by lazy { TripRepository(AppDatabase.getInstance(this).tripDao()) }
    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getInstance(this)
    }
}
