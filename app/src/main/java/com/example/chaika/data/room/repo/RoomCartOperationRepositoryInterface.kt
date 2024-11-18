package com.example.chaika.data.room.repo

import com.example.chaika.domain.models.CartOperationDomain
import kotlinx.coroutines.flow.Flow

interface RoomCartOperationRepositoryInterface {
    fun getAllOperations(): Flow<List<CartOperationDomain>>
}
