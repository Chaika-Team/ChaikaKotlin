package com.example.chaika.data.room.repo

import com.example.chaika.data.room.dao.CartOperationDao
import com.example.chaika.data.room.mappers.toDomain
import com.example.chaika.domain.models.CartOperationDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomCartOperationRepository @Inject constructor(
    private val cartOperationDao: CartOperationDao
) : RoomCartOperationRepositoryInterface {

    override fun getAllOperations(): Flow<List<CartOperationDomain>> {
        return cartOperationDao.getAllOperations().map { operations ->
            operations.map { it.toDomain() }
        }
    }
}
