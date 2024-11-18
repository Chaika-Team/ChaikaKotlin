package com.example.chaika.data.room.repo

import com.example.chaika.domain.models.CartItemDomain
import kotlinx.coroutines.flow.Flow

interface RoomCartItemRepositoryInterface {
    fun getItemsByOperationId(operationId: Int): Flow<List<CartItemDomain>>
}
