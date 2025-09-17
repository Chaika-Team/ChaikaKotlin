package com.chaikasoft.app.data.room.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.chaikasoft.app.data.room.dao.CartOperationDao
import com.chaikasoft.app.data.room.mappers.toCartDomain
import com.chaikasoft.app.data.room.mappers.toDomain
import com.chaikasoft.app.data.room.mappers.toInt
import com.chaikasoft.app.data.room.mappers.toReportPair
import com.chaikasoft.app.domain.models.CartDomain
import com.chaikasoft.app.domain.models.OperationSummaryDomain
import com.chaikasoft.app.domain.models.OperationTypeDomain
import com.chaikasoft.app.domain.models.report.CartOperationReport
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomCartOperationRepository @Inject constructor(
    private val cartOperationDao: CartOperationDao,
) : RoomCartOperationRepositoryInterface {

    override fun getCartOperationReportsWithIds(): Flow<List<Pair<Int, CartOperationReport>>> {
        return cartOperationDao
            .getOperationsWithConductorForReport()
            .map { rows -> rows.map { it.toReportPair() } } // <- маппер
    }

    override fun getPagedOperationSummaries(config: PagingConfig): Flow<PagingData<OperationSummaryDomain>> {
        return Pager(
            config = config,
            pagingSourceFactory = { cartOperationDao.getPagedOperationInfos() }
        ).flow.map { pagingData -> pagingData.map { it.toDomain() } }
    }

    override fun observeOperationItems(operationId: Int): Flow<CartDomain> {
            return cartOperationDao.observeItemsWithProducts(operationId)
                .map { it.toCartDomain() }
        }

    override suspend fun countByType(type: OperationTypeDomain): Int =
        cartOperationDao.countByType(type.toInt())

    override fun getPagedOperationSummariesByType(
        type: OperationTypeDomain,
        pageSize: Int
    ): Flow<PagingData<OperationSummaryDomain>> =
        Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false,
                initialLoadSize = pageSize * 2,
                prefetchDistance = pageSize
            ),
            pagingSourceFactory = { cartOperationDao.getPagedOperationInfosByType(type.toInt()) }
        ).flow.map { paging -> paging.map { it.toDomain() } }
}
