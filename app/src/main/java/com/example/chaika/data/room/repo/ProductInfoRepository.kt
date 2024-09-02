package com.example.chaika.data.room.repo

import com.example.chaika.data.room.dao.ProductInfoDao
import com.example.chaika.data.room.mappers.toDomain
import com.example.chaika.data.room.mappers.toEntity
import com.example.chaika.domain.models.ProductInfo

class ProductInfoRepository(private val productInfoDao: ProductInfoDao) {

    suspend fun getAllProducts(): List<ProductInfo> {
        return productInfoDao.getAllProducts().map { it.toDomain() }
    }

//    suspend fun getProductById(id: Int): ProductInfo? {
//        return productInfoDao.getProductById(id)?.toDomain()
//    }

    suspend fun insertProduct(product: ProductInfo) {
        productInfoDao.insertProduct(product.toEntity())
    }

    suspend fun updateProduct(product: ProductInfo) {
        productInfoDao.updateProduct(product.toEntity())
    }

    suspend fun deleteProduct(product: ProductInfo) {
        productInfoDao.deleteProduct(product.toEntity())
    }
}