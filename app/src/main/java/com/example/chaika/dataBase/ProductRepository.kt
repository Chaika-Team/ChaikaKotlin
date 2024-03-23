package com.example.chaika.dataBase

import androidx.lifecycle.LiveData
import com.example.chaika.dataBase.dao.ProductDao
import com.example.chaika.dataBase.entities.Product

class ProductRepository(private val productDao: ProductDao) {

    // LiveData со списком всех продуктов
    val allProducts: LiveData<List<Product>> = productDao.getAllProducts()

    // Функция для асинхронного добавления продукта (если нужно)
    suspend fun insert(product: Product) {
        productDao.insert(product)
    }

    suspend fun delete(product: Product) {
        productDao.delete(product)
    }

}
