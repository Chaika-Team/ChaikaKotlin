package com.example.chaika.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.chaika.domain.models.ProductInfoDomain
import com.example.chaika.domain.usecases.FetchAndSaveProductsUseCase
import com.example.chaika.domain.usecases.GetPagedProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getPagedProductsUseCase: GetPagedProductsUseCase,
    private val fetchAndSaveProductsUseCase: FetchAndSaveProductsUseCase,
) : ViewModel() {

    // PagingData, который получает продукты из базы через PagingSource
    val productsFlow: Flow<PagingData<ProductInfoDomain>> = getPagedProductsUseCase()

    // Функция для загрузки данных с сервера и сохранения их в БД
    fun refreshProducts(limit: Int = 100, offset: Int = 0) {
        viewModelScope.launch {
            try {
                fetchAndSaveProductsUseCase(limit, offset)
            } catch (e: Exception) {
                // Можно добавить LiveData для передачи ошибки в UI, если потребуется.
            }
        }
    }
}
