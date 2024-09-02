package com.example.chaika.data.room.repo.old

import com.example.chaika.domain.models.old.ProductInTrip
import com.example.chaika.data.room.dao.old.ProductDao
import com.example.chaika.data.room.entities.old.Product
import kotlinx.coroutines.flow.Flow

class ProductRepository(private val productDao: ProductDao) {

    suspend fun getProductsByTrip(tripId: Int): List<ProductInTrip> {
        return productDao.getProductsByTrip(tripId)
    }

    suspend fun getAllProducts(): Flow<List<Product>> {
        return productDao.getAllProducts()
    }

    suspend fun insertAll(products: List<Product>) {
        productDao.insertAll(products)
    }

    suspend fun getProductCount(): Int {
        return productDao.getProductCount()
    }
}
