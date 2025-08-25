package com.example.chaika.data.room.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.chaika.data.room.dao.CartOperationDao
import com.example.chaika.data.room.mappers.toCartDomain
import com.example.chaika.data.room.mappers.toDomain
import com.example.chaika.data.room.mappers.toReport
import com.example.chaika.domain.models.CartDomain
import com.example.chaika.domain.models.OperationSummaryDomain
import com.example.chaika.domain.models.report.CartOperationReport
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomCartOperationRepository @Inject constructor(
    private val cartOperationDao: CartOperationDao,
) : RoomCartOperationRepositoryInterface {

    override fun getCartOperationReportsWithIds(): Flow<List<Pair<Int, CartOperationReport>>> {
        return cartOperationDao.getAllOperations().map { operations ->
            operations.map { operation ->
                operation.id to operation.toReport(emptyList())
            }
        }
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
}
