package com.chaikasoft.app.data.room.repo

import com.chaikasoft.app.domain.models.CartDomain
import com.chaikasoft.app.domain.models.CartOperationDomain

@Suppress("FunctionInterface")
interface RoomCartRepositoryInterface {
    suspend fun saveCartWithItemsAndOperation(
        cart: CartDomain,
        cartOperationDomain: CartOperationDomain,
    )
}
