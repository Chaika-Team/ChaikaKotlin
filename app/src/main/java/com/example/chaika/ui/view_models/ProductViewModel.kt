package com.example.chaika.ui.view_models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chaika.domain.models.ProductInfoDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor() : ViewModel() {

    private val _products = MutableLiveData<List<ProductInfoDomain>>()
    val products: LiveData<List<ProductInfoDomain>> get() = _products

    // Метод для загрузки фиктивных данных

}
