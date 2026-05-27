package com.chaikasoft.app.data.room.repo

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.chaikasoft.app.domain.models.CartDomain
import com.chaikasoft.app.domain.models.OperationSummaryDomain
import com.chaikasoft.app.domain.models.OperationTypeDomain
import com.chaikasoft.app.domain.models.report.CartOperationReportHeader
import kotlinx.coroutines.flow.Flow

@Suppress("FunctionInterface")
interface RoomCartOperationRepositoryInterface {
    fun getCartOperationReportHeadersWithIds(): Flow<List<Pair<Int, CartOperationReportHeader>>>

    // Пагинация «шапок» операций
    fun getPagedOperationSummaries(config: PagingConfig): Flow<PagingData<OperationSummaryDomain>>

    // Дозагрузка товаров по операции
    fun observeOperationItems(operationId: Int): Flow<CartDomain>

    // Количество операций по выбранному типу
    suspend fun countByType(type: OperationTypeDomain): Int

    // Операции по выбранному типу
    fun getPagedOperationSummariesByType(
        type: OperationTypeDomain,
        pageSize: Int
    ): Flow<PagingData<OperationSummaryDomain>>

    suspend fun clearAllOperations()
}
