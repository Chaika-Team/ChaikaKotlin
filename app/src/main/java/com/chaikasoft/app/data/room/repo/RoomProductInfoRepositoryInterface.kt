package com.chaikasoft.app.data.room.repo

import androidx.paging.PagingData
import com.chaikasoft.app.domain.models.ProductInfoDomain
import kotlinx.coroutines.flow.Flow

interface RoomProductInfoRepositoryInterface {
    fun getAllProducts(): Flow<List<ProductInfoDomain>>
    suspend fun insertProduct(product: ProductInfoDomain)
    suspend fun upsertAll(products: List<ProductInfoDomain>)
    suspend fun updateProduct(product: ProductInfoDomain)
    suspend fun deleteProduct(product: ProductInfoDomain)
    suspend fun hasAnyProductsOnce(): Boolean
    suspend fun getAllProductsOnce(): List<ProductInfoDomain>
    fun getPagedProducts(
        query: String? = null,
        pageSize: Int = 20
    ): Flow<PagingData<ProductInfoDomain>>
    suspend fun getProductById(productId: Int): ProductInfoDomain?
    suspend fun getProductsByIds(productIds: Collection<Int>): List<ProductInfoDomain>
}
