package com.example.chaika.data.room.repo

import com.example.chaika.domain.models.report.CartOperationReport
import kotlinx.coroutines.flow.Flow

@Suppress("FunctionInterface")
interface RoomCartOperationRepositoryInterface {
    fun getCartOperationReportsWithIds(): Flow<List<Pair<Int, CartOperationReport>>>
}
