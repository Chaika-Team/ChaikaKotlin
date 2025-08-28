package com.example.chaika.data.room.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.map
import com.example.chaika.data.room.dao.ProductInfoDao
import com.example.chaika.data.room.mappers.toDomain
import com.example.chaika.data.room.mappers.toEntity
import com.example.chaika.domain.models.ProductInfoDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomProductInfoRepository @Inject constructor(
    private val productInfoDao: ProductInfoDao,
) : RoomProductInfoRepositoryInterface {

    override fun getAllProducts(): Flow<List<ProductInfoDomain>> {
        return productInfoDao.getAllProducts().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun insertProduct(product: ProductInfoDomain) {
        productInfoDao.upsertProduct(product.toEntity())
    }

    override suspend fun updateProduct(product: ProductInfoDomain) {
        productInfoDao.updateProduct(product.toEntity())
    }

    override suspend fun deleteProduct(product: ProductInfoDomain) {
        productInfoDao.deleteProduct(product.toEntity())
    }

    override fun getPagedProducts(config: PagingConfig): Flow<PagingData<ProductInfoDomain>> {
        return Pager(
            config = config,
            pagingSourceFactory = { productInfoDao.getPagedProducts() }
        )
            .flow
            .map { pagingData -> pagingData.map { it.toDomain() } }
    }

    override suspend fun getProductById(productId: Int): ProductInfoDomain? {
        return productInfoDao.getProductById(productId)?.toDomain()
    }

}
