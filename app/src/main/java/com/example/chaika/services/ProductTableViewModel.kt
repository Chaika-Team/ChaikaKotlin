package com.example.chaika.services

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chaika.utils.ProductInTrip
import com.example.chaika.dataBase.models.ActionRepository
import com.example.chaika.dataBase.models.ProductRepository
import kotlinx.coroutines.launch

class ProductTableViewModel(
    private val productRepository: ProductRepository,
    private val actionRepository: ActionRepository
) : ViewModel() {

    val productsInTrip: LiveData<List<ProductInTrip>> = MutableLiveData()

    fun loadProductsByTrip(tripId: Int) {
        viewModelScope.launch {
            val products = productRepository.getProductsByTrip(tripId)
            (productsInTrip as MutableLiveData).postValue(products)
        }
    }

    fun deleteActionsForProductInTrip(productId: Int, tripId: Int) {
        viewModelScope.launch {
            actionRepository.deleteActionsForProductInTrip(productId, tripId)
            loadProductsByTrip(tripId)
        }
    }

    fun addAction(tripId: Int, productId: Int, operationId: Int, count: Int) {
        viewModelScope.launch {
            actionRepository.addAction(tripId, productId, operationId, count)
            loadProductsByTrip(tripId)
        }
    }
}
