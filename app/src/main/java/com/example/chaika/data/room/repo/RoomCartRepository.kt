package com.example.chaika.data.room.repo

import androidx.room.Transaction
import com.example.chaika.data.room.dao.CartItemDao
import com.example.chaika.data.room.dao.CartOperationDao
import com.example.chaika.data.room.mappers.toEntity
import com.example.chaika.domain.models.CartDomain
import com.example.chaika.domain.models.CartOperationDomain
import javax.inject.Inject

class RoomCartRepository @Inject constructor(
    private val cartItemDao: CartItemDao,
    private val cartOperationDao: CartOperationDao,
) : RoomCartRepositoryInterface {

    @Transaction
    override suspend fun saveCartWithItemsAndOperation(
        cart: CartDomain,
        cartOperationDomain: CartOperationDomain,
    ) {
        // Вставляем операцию и получаем её ID
        val cartOperationId = cartOperationDao.insertOperation(cartOperationDomain.toEntity())

        // Вставляем товары в корзину с учетом типа операции и привязываем к cartOperationId
        cart.items.forEach { cartItem ->
            cartItemDao.insertCartItem(
                cartItem.toEntity(
                    cartOperationId.toInt(),
                    cartOperationDomain.operationTypeDomain,
                ),
            )
        }
    }
}
