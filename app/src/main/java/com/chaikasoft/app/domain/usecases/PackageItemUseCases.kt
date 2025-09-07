package com.chaikasoft.app.domain.usecases

import com.chaikasoft.app.data.room.repo.RoomPackageItemRepositoryInterface
import com.chaikasoft.app.domain.models.PackageItemDomain
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
