package com.example.chaika.domain.usecases

import com.example.chaika.data.room.repo.RoomPackageItemRepositoryInterface
import com.example.chaika.domain.models.PackageItemDomain
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPackageItemUseCase @Inject constructor(
    private val repository: RoomPackageItemRepositoryInterface,
) {
    operator fun invoke(): Flow<List<PackageItemDomain>> {
        return repository.getAllPackageItems()
    }
}

class GetAvailableQuantityUseCase @Inject constructor(
    private val repository: RoomPackageItemRepositoryInterface
) {
    suspend operator fun invoke(productId: Int): Int =
        repository.getCurrentQuantity(productId)
}
