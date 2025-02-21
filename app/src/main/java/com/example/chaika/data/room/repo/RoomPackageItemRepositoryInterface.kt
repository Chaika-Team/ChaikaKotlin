package com.example.chaika.data.room.repo

import com.example.chaika.domain.models.PackageItemDomain
import kotlinx.coroutines.flow.Flow

interface RoomPackageItemRepositoryInterface {
    fun getAllPackageItems(): Flow<List<PackageItemDomain>>
    suspend fun getPackageItemByProductId(productId: Int): PackageItemDomain?
}
