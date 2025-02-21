package com.example.chaika.data.room.repo

import com.example.chaika.data.room.dao.PackageItemViewDao
import com.example.chaika.data.room.dao.ProductInfoDao
import com.example.chaika.data.room.mappers.toDomain
import com.example.chaika.domain.models.PackageItemDomain
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
}
