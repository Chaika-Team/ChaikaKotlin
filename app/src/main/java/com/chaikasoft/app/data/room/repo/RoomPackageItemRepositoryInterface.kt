package com.chaikasoft.app.data.room.repo

import com.chaikasoft.app.domain.models.PackageItemDomain
import kotlinx.coroutines.flow.Flow

interface RoomPackageItemRepositoryInterface {
    fun getAllPackageItems(): Flow<List<PackageItemDomain>>
    suspend fun getPackageItemByProductId(productId: Int): PackageItemDomain?
    suspend fun getCurrentQuantity(productId: Int): Int

    suspend fun hasAnyPackageItemsOnce(): Boolean
}
