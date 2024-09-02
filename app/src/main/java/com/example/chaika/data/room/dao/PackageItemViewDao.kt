package com.example.chaika.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.chaika.data.room.entities.PackageItemView

@Dao
interface PackageItemViewDao {

    @Query("SELECT * FROM package_items")
    suspend fun getPackageItems(): List<PackageItemView>
}
