package com.example.chaika.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.chaika.domain.models.CartDomain
import com.example.chaika.domain.usecases.GetPagedOperationSummariesUseCase
import com.example.chaika.domain.usecases.ObserveOperationItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class OperationViewModel @Inject constructor(
    private val getPagedOperationSummaries: GetPagedOperationSummariesUseCase,
    private val observeOperationItems: ObserveOperationItemsUseCase
) : ViewModel() {

    val operations = getPagedOperationSummaries().cachedIn(viewModelScope)

    fun getItems(operationId: Int): Flow<CartDomain> =
        observeOperationItems(operationId)
}
