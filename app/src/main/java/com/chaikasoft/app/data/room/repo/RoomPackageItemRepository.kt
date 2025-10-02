package com.chaikasoft.app.data.room.repo

import com.chaikasoft.app.data.room.dao.PackageItemViewDao
import com.chaikasoft.app.data.room.dao.ProductInfoDao
import com.chaikasoft.app.data.room.mappers.toDomain
import com.chaikasoft.app.domain.models.PackageItemDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomPackageItemRepository @Inject constructor(
    private val packageItemViewDao: PackageItemViewDao,
    private val productInfoDao: ProductInfoDao,
) : RoomPackageItemRepositoryInterface {

    // Получение всех товаров у проводника из представления PackageItemView с использованием Flow
    override fun getAllPackageItems(): Flow<List<PackageItemDomain>> {
        return packageItemViewDao.getPackageItems().map { packageItemViews ->
            packageItemViews.mapNotNull { packageItemView ->
                // Получаем сущность ProductInfo из базы данных
                val productInfoEntity = productInfoDao.getProductById(packageItemView.productId)

                // Преобразуем сущность ProductInfo в доменную модель и PackageItemView в PackageItem
                productInfoEntity?.toDomain()?.let { productInfo ->
                    packageItemView.toDomain(productInfo)
                }
            }
        }
    }

    override suspend fun getPackageItemByProductId(productId: Int): PackageItemDomain? {
        val packageItemView = packageItemViewDao.getPackageItemByProductId(productId)
        val productInfoEntity = productInfoDao.getProductById(productId)
        return productInfoEntity?.toDomain()?.let { packageItemView?.toDomain(it) }
    }

    override suspend fun getCurrentQuantity(productId: Int): Int {
        val packageItemView = packageItemViewDao.getPackageItemByProductId(productId)
        return packageItemView?.currentQuantity ?: 0
    }

    override suspend fun hasAnyPackageItemsOnce(): Boolean =
        packageItemViewDao.hasAnyOnce()
}
