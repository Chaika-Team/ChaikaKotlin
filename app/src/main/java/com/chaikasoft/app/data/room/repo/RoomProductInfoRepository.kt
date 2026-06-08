package com.chaikasoft.app.data.room.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.chaikasoft.app.data.room.dao.ProductInfoDao
import com.chaikasoft.app.data.room.mappers.toDomain
import com.chaikasoft.app.data.room.mappers.toEntity
import com.chaikasoft.app.domain.models.ProductInfoDomain
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomProductInfoRepository @Inject constructor(private val productInfoDao: ProductInfoDao) :
    RoomProductInfoRepositoryInterface {

    override fun getAllProducts(): Flow<List<ProductInfoDomain>> =
        productInfoDao.getAllProducts().map { list ->
            list.map { it.toDomain() }
        }

    override suspend fun insertProduct(product: ProductInfoDomain) {
        productInfoDao.upsertProduct(product.toEntity())
    }

    override suspend fun upsertAll(products: List<ProductInfoDomain>) {
        productInfoDao.upsertAll(products.map { it.toEntity() })
    }

    override suspend fun updateProduct(product: ProductInfoDomain) {
        productInfoDao.updateProduct(product.toEntity())
    }

    override suspend fun deleteProduct(product: ProductInfoDomain) {
        productInfoDao.deleteProduct(product.toEntity())
    }

    override suspend fun hasAnyProductsOnce(): Boolean = productInfoDao.hasAnyProductsOnce()

    override suspend fun getAllProductsOnce(): List<ProductInfoDomain> =
        productInfoDao.getAllProductsOnce().map { it.toDomain() }

    override fun getPagedProducts(
        query: String?,
        pageSize: Int
    ): Flow<PagingData<ProductInfoDomain>> = Pager(
        config = PagingConfig(
            pageSize = pageSize,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            productInfoDao.getPagedProducts(query)
        }
    ).flow.map { pagingData ->
        pagingData.map { it.toDomain() }
    }

    override suspend fun getProductById(productId: Int): ProductInfoDomain? =
        productInfoDao.getProductById(productId)?.toDomain()

    override suspend fun getProductsByIds(productIds: Collection<Int>): List<ProductInfoDomain> {
        if (productIds.isEmpty()) return emptyList()
        return productInfoDao.getProductsByIds(productIds.toList()).map { it.toDomain() }
    }
}
