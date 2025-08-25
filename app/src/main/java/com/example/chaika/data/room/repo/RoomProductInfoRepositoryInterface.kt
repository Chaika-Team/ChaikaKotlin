package com.example.chaika.data.room.repo

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.chaika.domain.models.ProductInfoDomain
import kotlinx.coroutines.flow.Flow

interface RoomProductInfoRepositoryInterface {
    fun getAllProducts(): Flow<List<ProductInfoDomain>>
    suspend fun insertProduct(product: ProductInfoDomain)
    suspend fun updateProduct(product: ProductInfoDomain)
    suspend fun deleteProduct(product: ProductInfoDomain)
    fun getPagedProducts(config: PagingConfig): Flow<PagingData<ProductInfoDomain>>
    suspend fun getProductById(productId: Int): ProductInfoDomain?

}
