package com.chaikasoft.app.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaikasoft.app.domain.models.FastReportDomain
import com.chaikasoft.app.domain.models.OperationTypeDomain
import com.chaikasoft.app.domain.usecases.GetFastReportDataUseCase
import com.chaikasoft.app.domain.usecases.GetOperationCountByTypeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    getFastReportDataUseCase: GetFastReportDataUseCase,
    private val getOperationCountByType: GetOperationCountByTypeUseCase
) : ViewModel() {

    val reports: StateFlow<List<FastReportDomain>> =
        getFastReportDataUseCase()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    /** Выручка по наличным = сумма (цена * продано наличными) */
    val cashRevenue: StateFlow<Double> =
        reports
            .map { list -> list.sumOf { it.productPrice * it.soldCashQuantity } }
            .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    private val _cashChecksCount = MutableStateFlow(0)
    val cashChecksCount: StateFlow<Int> = _cashChecksCount.asStateFlow()

    /** Вызывать при старте экрана и, при желании, при расширении шторки */
    fun refreshCashChecks() {
        viewModelScope.launch {
            _cashChecksCount.value = getOperationCountByType(OperationTypeDomain.SOLD_CASH)
        }
    }
}
