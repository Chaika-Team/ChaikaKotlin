package com.chaikasoft.app.domain.usecases

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.chaikasoft.app.data.room.repo.RoomCartOperationRepositoryInterface
import com.chaikasoft.app.domain.models.CartDomain
import com.chaikasoft.app.domain.models.OperationSummaryDomain
import com.chaikasoft.app.domain.models.OperationTypeDomain
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case для получения пагинированного списка "шапок" операций, без продуктов.
 */

class GetPagedOperationSummariesUseCase @Inject constructor(
    private val repository: RoomCartOperationRepositoryInterface,
) {
    operator fun invoke(
        pageSize: Int = 20,
        enablePlaceholders: Boolean = false
    ): Flow<PagingData<OperationSummaryDomain>> {
        val config = PagingConfig(
            pageSize = pageSize,
            enablePlaceholders = enablePlaceholders
        )
        return repository.getPagedOperationSummaries(config)
    }
}

/**
 * Use case для дозагрузки товаров по спагинированным операциям.
 */
class ObserveOperationItemsUseCase @Inject constructor(
    private val repository: RoomCartOperationRepositoryInterface,
) {
    operator fun invoke(operationId: Int): Flow<CartDomain> =
        repository.observeOperationItems(operationId)
}

/**
 * Единовременный подсчёт числа операций нужного типа
 */
class GetOperationCountByTypeUseCase @Inject constructor(
    private val repo: RoomCartOperationRepositoryInterface
) {
    suspend operator fun invoke(type: OperationTypeDomain): Int =
        repo.countByType(type)
}

/**
 * Пагинированный список «шапок» операций нужного типа
 */
class GetPagedOperationSummariesByTypeUseCase @Inject constructor(
    private val repo: RoomCartOperationRepositoryInterface
) {
    operator fun invoke(
        type: OperationTypeDomain,
        pageSize: Int = 20
    ): Flow<PagingData<OperationSummaryDomain>> =
        repo.getPagedOperationSummariesByType(type, pageSize)
}