package com.chaikasoft.app.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.chaikasoft.app.domain.models.CartDomain
import com.chaikasoft.app.domain.usecases.GetPagedOperationSummariesUseCase
import com.chaikasoft.app.domain.usecases.ObserveOperationItemsUseCase
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
