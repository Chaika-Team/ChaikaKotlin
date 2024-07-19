package com.example.chaika.services

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chaika.activities.productTableActivity.ProductInTrip
import com.example.chaika.dataBase.entities.Product
import com.example.chaika.dataBase.models.ProductRepository

import kotlinx.coroutines.launch

class ProductViewModel(private val productRepository: ProductRepository) : ViewModel() {

    private val _productsInTrip = MutableLiveData<List<ProductInTrip>>()
    val productsInTrip: LiveData<List<ProductInTrip>> get() = _productsInTrip

    private val _allProducts = MutableLiveData<List<Product>>()
    val allProducts: LiveData<List<Product>> get() = _allProducts

    fun loadProductsByTrip(tripId: Int) {
        viewModelScope.launch {
            val productList = productRepository.getProductsByTrip(tripId)
            _productsInTrip.value = productList
        }
    }

    fun loadAllProducts() {
        viewModelScope.launch {
            val productList = productRepository.getAllProducts()
            _allProducts.value = productList
        }
    }

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
}
