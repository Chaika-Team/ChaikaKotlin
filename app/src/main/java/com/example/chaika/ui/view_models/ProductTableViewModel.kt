package com.example.chaika.ui.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chaika.domain.usecases.AddProductActionUseCase
import com.example.chaika.domain.usecases.DeleteActionsForProductInTripUseCase
import com.example.chaika.domain.usecases.GetProductsByTripUseCase
import com.example.chaika.models.ProductInTrip
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductTableViewModel @Inject constructor(
    private val getProductsByTripUseCase: GetProductsByTripUseCase,
    private val deleteActionsForProductInTripUseCase: DeleteActionsForProductInTripUseCase,
    private val addProductActionUseCase: AddProductActionUseCase
) : ViewModel() {

    val productsInTrip: LiveData<List<ProductInTrip>> = MutableLiveData()

    fun loadProductsByTrip(tripId: Int) {
        viewModelScope.launch {
            val products = getProductsByTripUseCase.execute(tripId)
            (productsInTrip as MutableLiveData).postValue(products)
        }
    }

    fun deleteActionsForProductInTrip(productId: Int, tripId: Int) {
        viewModelScope.launch {
            deleteActionsForProductInTripUseCase.execute(productId, tripId)
            loadProductsByTrip(tripId)
        }
    }

    fun addAction(tripId: Int, productId: Int, operationId: Int, count: Int) {
        viewModelScope.launch {
            addProductActionUseCase.execute(tripId, productId, operationId, count)
            loadProductsByTrip(tripId)
        }
    }
}
