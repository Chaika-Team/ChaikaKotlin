package com.chaikasoft.app.data.room.repo

import com.chaikasoft.app.domain.models.report.CartItemReport
import kotlinx.coroutines.flow.Flow

@Suppress("FunctionInterface")
interface RoomCartItemRepositoryInterface {
    fun getCartItemReportsByOperationId(operationId: Int): Flow<List<CartItemReport>>
}
