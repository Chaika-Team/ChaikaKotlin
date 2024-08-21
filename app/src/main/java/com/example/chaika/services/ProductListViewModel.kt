package com.example.chaika.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chaika.dataBase.entities.Product
import com.example.chaika.usecases.AddProductActionUseCase
import com.example.chaika.usecases.FilterProductsUseCase
import com.example.chaika.usecases.GetAllProductsUseCase
import com.example.chaika.usecases.InsertProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val getAllProductsUseCase: GetAllProductsUseCase,
    private val insertProductsUseCase: InsertProductsUseCase,
    private val addProductActionUseCase: AddProductActionUseCase,
    private val filterProductsUseCase: FilterProductsUseCase
) : ViewModel() {

    private val _allProducts = MutableStateFlow<List<Product>>(emptyList())
    private val _filteredProducts = MutableStateFlow<List<Product>>(emptyList())
    val filteredProducts: StateFlow<List<Product>> = _filteredProducts.asStateFlow()

    init {
        viewModelScope.launch {
            initializeProducts()
            getAllProductsUseCase.execute().collect { products ->
                _allProducts.value = products
                _filteredProducts.value = products
            }
        }
    }

    fun addAction(tripId: Int, productId: Int, operationId: Int, count: Int) = viewModelScope.launch {
        addProductActionUseCase.execute(tripId, productId, operationId, count)
    }

    fun filterProducts(query: String) {
        if (query.isEmpty()) {
            _filteredProducts.value = _allProducts.value
        } else {
            _filteredProducts.value = filterProductsUseCase.execute(_allProducts.value, query)
        }
    }

    private fun initializeProducts() = viewModelScope.launch {
        val productCount = _allProducts.value.size
        if (productCount == 0) {
            val initialProducts = listOf(
                Product(id = 1, title = "Круассаны", price = 110.0),
                Product(id = 2, title = "Фисташки", price = 55.0),
                Product(id = 3, title = "Кофе Жокей", price = 30.0),
                Product(id = 4, title = "Чай чёрный", price = 25.0),
                Product(id = 5, title = "Сахар", price = 10.0)
            )
            insertProductsUseCase.execute(initialProducts)
        }
    }
}
