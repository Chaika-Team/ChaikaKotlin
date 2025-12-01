package com.chaikasoft.app.data.room.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.chaikasoft.app.data.room.dao.ProductInfoDao
import com.chaikasoft.app.data.room.mappers.toDomain
import com.chaikasoft.app.data.room.mappers.toEntity
import com.chaikasoft.app.domain.models.ProductInfoDomain
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

    override fun getPagedProducts(
        query: String?,
        pageSize: Int
    ): Flow<PagingData<ProductInfoDomain>> {
        val config = PagingConfig(
            pageSize = pageSize,
            enablePlaceholders = false,
            initialLoadSize = pageSize * 2,
            prefetchDistance = pageSize
        )

        return if (query.isNullOrBlank()) {
            // кейс «все продукты»
            Pager(
                config = config,
                pagingSourceFactory = { productInfoDao.getPagedProducts() }
            ).flow.map { paging -> paging.map { it.toDomain() } }
        } else {
            // кейс «поиск по имени»
            val pattern = "${escapeLike(query)}%"

            Pager(
                config = config,
                pagingSourceFactory = { productInfoDao.getPagedProductsByName(pattern) }
            ).flow.map { paging -> paging.map { it.toDomain() } }
        }
    }

    private fun escapeLike(s: String): String =
        s.replace("\\", "\\\\")
            .replace("%", "\\%")
            .replace("_", "\\_")

    override suspend fun getProductById(productId: Int): ProductInfoDomain? {
        return productInfoDao.getProductById(productId)?.toDomain()
    }

}
