package com.example.chaika.tests.new_tests

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.chaika.dataBase.AppDatabase
import com.example.chaika.dataBase.ProductRepository
import com.example.chaika.dataBase.entities.Product
import kotlinx.coroutines.launch

class ProductViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ProductRepository

    // LiveData, содержащая список продуктов
    val allProducts: LiveData<List<Product>>

    fun insert(product: Product) = viewModelScope.launch {
        repository.insert(product)
    }

    fun delete(product: Product) = viewModelScope.launch {
        repository.delete(product)
    }


    init {
        // Получаем ссылку на ProductDao из AppDatabase
        val productDao = AppDatabase.getInstance(application).productDao()
        // Инициализируем репозиторий
        repository = ProductRepository(productDao)
        // Получаем LiveData со списком продуктов
        allProducts = repository.allProducts
    }
}