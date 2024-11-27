package com.example.chaika.data.room.repo

import com.example.chaika.data.room.dao.CartOperationDao
import com.example.chaika.data.room.mappers.toReport
import com.example.chaika.domain.models.CartOperationReport
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomCartOperationRepository @Inject constructor(
    private val cartOperationDao: CartOperationDao
) : RoomCartOperationRepositoryInterface {

    override fun getCartOperationReportsWithIds(): Flow<List<Pair<Int, CartOperationReport>>> {
        return cartOperationDao.getAllOperations().map { operations ->
            operations.map { operation ->
                operation.id to operation.toReport(emptyList()) // Используем маппер
            }
        }
    }
}

