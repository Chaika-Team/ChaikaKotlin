package com.example.chaika.data.room.repo

import com.example.chaika.domain.models.ProductInfoDomain
import kotlinx.coroutines.flow.Flow

interface RoomProductInfoRepositoryInterface {
    fun getAllProducts(): Flow<List<ProductInfoDomain>>
    suspend fun insertProduct(product: ProductInfoDomain)
    suspend fun updateProduct(product: ProductInfoDomain)
    suspend fun deleteProduct(product: ProductInfoDomain)
    // Добавьте метод для получения продукта по ID, если он будет нужен
    // suspend fun getProductById(id: Int): ProductInfoDomain?
}
