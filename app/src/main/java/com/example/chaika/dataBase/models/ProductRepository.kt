package com.example.chaika.dataBase.models

import com.example.chaika.utils.ProductInTrip
import com.example.chaika.dataBase.dao.ProductDao
import com.example.chaika.dataBase.entities.Product

class ProductRepository(private val productDao: ProductDao) {

    suspend fun getProductsByTrip(tripId: Int): List<ProductInTrip> {
        return productDao.getProductsByTrip(tripId)
    }

    suspend fun getAllProducts(): List<Product> {
        return productDao.getAllProducts()
    }

    suspend fun insertAll(products: List<Product>) {
        productDao.insertAll(products)
    }

    suspend fun getProductCount(): Int {
        return productDao.getProductCount()
    }
}
