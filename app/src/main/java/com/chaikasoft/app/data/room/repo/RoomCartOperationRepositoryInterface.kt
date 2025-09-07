package com.chaikasoft.app.data.room.repo

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.chaikasoft.app.domain.models.CartDomain
import com.chaikasoft.app.domain.models.OperationSummaryDomain
import com.chaikasoft.app.domain.models.report.CartOperationReport
import kotlinx.coroutines.flow.Flow

@Suppress("FunctionInterface")
interface RoomCartOperationRepositoryInterface {
    fun getCartOperationReportsWithIds(): Flow<List<Pair<Int, CartOperationReport>>>
    // Пагинация «шапок» операций
    fun getPagedOperationSummaries(config: PagingConfig): Flow<PagingData<OperationSummaryDomain>>

    // Дозагрузка товаров по операции
    fun observeOperationItems(operationId: Int): Flow<CartDomain>
}
