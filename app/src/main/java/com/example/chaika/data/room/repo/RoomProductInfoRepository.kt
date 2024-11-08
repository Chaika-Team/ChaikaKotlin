package com.example.chaika.data.room.repo

import com.example.chaika.data.room.dao.ProductInfoDao
import com.example.chaika.data.room.mappers.toDomain
import com.example.chaika.data.room.mappers.toEntity
import com.example.chaika.domain.models.ProductInfoDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomProductInfoRepository @Inject constructor(
    private val productInfoDao: ProductInfoDao
) : RoomProductInfoRepositoryInterface {

    override fun getAllProducts(): Flow<List<ProductInfoDomain>> {
        return productInfoDao.getAllProducts().map { list -> list.map { it.toDomain() } }
    }

    //    suspend fun getProductById(id: Int): ProductInfo? {
    //        return productInfoDao.getProductById(id)?.toDomain()
    //    }

    override suspend fun insertProduct(product: ProductInfoDomain) {
        productInfoDao.insertProduct(product.toEntity())
    }

    override suspend fun updateProduct(product: ProductInfoDomain) {
        productInfoDao.updateProduct(product.toEntity())
    }

    override suspend fun deleteProduct(product: ProductInfoDomain) {
        productInfoDao.deleteProduct(product.toEntity())
    }
}
