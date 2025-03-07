package com.example.chaika.data.room.repo

import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.example.chaika.domain.models.ProductInfoDomain
import kotlinx.coroutines.flow.Flow

interface RoomProductInfoRepositoryInterface {
    fun getAllProducts(): Flow<List<ProductInfoDomain>>
    suspend fun insertProduct(product: ProductInfoDomain)
    suspend fun updateProduct(product: ProductInfoDomain)
    suspend fun deleteProduct(product: ProductInfoDomain)
    fun getPagedProducts(): PagingSource<Int, ProductInfoDomain>
}
