package com.chaikasoft.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaikasoft.app.domain.models.FastReportDomain
import com.chaikasoft.app.domain.models.OperationTypeDomain
import com.chaikasoft.app.domain.usecases.GetFastReportDataUseCase
import com.chaikasoft.app.domain.usecases.GetOperationCountByTypeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    getFastReportDataUseCase: GetFastReportDataUseCase,
    private val getOperationCountByType: GetOperationCountByTypeUseCase
) : ViewModel() {

    val reports: StateFlow<List<FastReportDomain>> =
        getFastReportDataUseCase()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    /** Выручка по наличным = сумма (цена * продано наличными) */
    val cashRevenue: StateFlow<Int> =
        reports
            .map { list -> list.sumOf { it.productPrice * it.soldCashQuantity } }
            .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    private val _cashlessChecksCount = MutableStateFlow(0)
    val cashlessChecksCount: StateFlow<Int> = _cashlessChecksCount.asStateFlow()

    /** Вызывать при старте экрана и, при желании, при расширении шторки */
    fun refreshCashlessChecks() {
        viewModelScope.launch {
            _cashlessChecksCount.value = getOperationCountByType(OperationTypeDomain.SOLD_CART)
        }
    }
}
