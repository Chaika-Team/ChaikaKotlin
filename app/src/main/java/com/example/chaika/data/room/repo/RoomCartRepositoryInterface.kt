package com.example.chaika.data.room.repo

import com.example.chaika.domain.models.CartDomain
import com.example.chaika.domain.models.CartOperationDomain

interface RoomCartRepositoryInterface {
    suspend fun saveCartWithItemsAndOperation(
        cart: CartDomain,
        cartOperationDomain: CartOperationDomain
    )
}