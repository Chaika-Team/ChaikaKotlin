package com.example.chaika.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.chaika.dataBase.models.ActionRepository
import com.example.chaika.dataBase.models.ProductRepository

class ProductListViewModelFactory(
    private val productRepository: ProductRepository,
    private val actionRepository: ActionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductListViewModel(productRepository, actionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
