package com.example.chaika.data.room.repo

import com.example.chaika.data.room.dao.CartItemDao
import com.example.chaika.data.room.mappers.toDomain
import com.example.chaika.domain.models.CartItemDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomCartItemRepository @Inject constructor(
    private val cartItemDao: CartItemDao
) : RoomCartItemRepositoryInterface {

    override fun getItemsByOperationId(operationId: Int): Flow<List<CartItemDomain>> {
        return cartItemDao.getCartItemsByCartOpId(operationId).map { items ->
            items.map { it.toDomain() }
        }
    }
}
