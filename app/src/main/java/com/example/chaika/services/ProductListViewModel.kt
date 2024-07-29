package com.example.chaika.services

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chaika.dataBase.entities.Product
import com.example.chaika.dataBase.models.ActionRepository
import com.example.chaika.dataBase.models.ProductRepository
import kotlinx.coroutines.launch

class ProductListViewModel(
    private val productRepository: ProductRepository,
    private val actionRepository: ActionRepository
) : ViewModel() {

    private val _allProducts = MutableLiveData<List<Product>>()
    val allProducts: LiveData<List<Product>> = _allProducts

    fun initializeProducts() {
        viewModelScope.launch {
            val productCount = productRepository.getProductCount()
            if (productCount == 0) {
                val initialProducts = listOf(
                    Product(id = 1, title = "Круассаны", price = 110.0),
                    Product(id = 2, title = "Фисташки", price = 55.0),
                    Product(id = 3, title = "Кофе Жокей", price = 30.0),
                    Product(id = 4, title = "Чай чёрный", price = 25.0),
                    Product(id = 5, title = "Сахар", price = 10.0)
                )
                productRepository.insertAll(initialProducts)
            }
        }
    }

    fun loadAllProducts() {
        viewModelScope.launch {
            val products = productRepository.getAllProducts()
            _allProducts.postValue(products)
        }
    }

    fun addAction(tripId: Int, productId: Int, operationId: Int, count: Int) {
        viewModelScope.launch {
            actionRepository.addAction(tripId, productId, operationId, count)
        }
    }
}
