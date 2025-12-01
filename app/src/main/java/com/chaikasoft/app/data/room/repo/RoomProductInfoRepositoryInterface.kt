package com.chaikasoft.app.data.room.repo

import androidx.paging.PagingData
import com.chaikasoft.app.domain.models.ProductInfoDomain
import kotlinx.coroutines.flow.Flow

interface RoomProductInfoRepositoryInterface {
    fun getAllProducts(): Flow<List<ProductInfoDomain>>
    suspend fun insertProduct(product: ProductInfoDomain)
    suspend fun updateProduct(product: ProductInfoDomain)
    suspend fun deleteProduct(product: ProductInfoDomain)
    fun getPagedProducts(
        query: String? = null, pageSize: Int = 20
    ): Flow<PagingData<ProductInfoDomain>>
    suspend fun getProductById(productId: Int): ProductInfoDomain?

}
