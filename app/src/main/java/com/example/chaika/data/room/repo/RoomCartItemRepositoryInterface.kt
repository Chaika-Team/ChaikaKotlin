package com.example.chaika.data.room.repo

import com.example.chaika.domain.models.CartItemReport
import kotlinx.coroutines.flow.Flow

interface RoomCartItemRepositoryInterface {
    fun getCartItemReportsByOperationId(operationId: Int): Flow<List<CartItemReport>>
}
