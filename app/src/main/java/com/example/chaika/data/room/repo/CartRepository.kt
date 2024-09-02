package com.example.chaika.data.room.repo

import androidx.room.Transaction
import com.example.chaika.data.room.dao.CartItemDao
import com.example.chaika.data.room.dao.CartOperationDao
import com.example.chaika.data.room.entities.CartOperation as CartOperationEntity
import com.example.chaika.data.room.mappers.toEntity
import com.example.chaika.domain.models.Cart
import com.example.chaika.domain.models.CartOperation
import javax.inject.Inject

class CartRepository @Inject constructor(
    private val cartItemDao: CartItemDao,
    private val cartOperationDao: CartOperationDao
) {

    @Transaction
    suspend fun saveCartWithItemsAndOperation(cart: Cart, cartOperation: CartOperation) {
        // Вставляем операцию и получаем её ID
        val cartOperationId = cartOperationDao.insertOperation(cartOperation.toEntity())

        // Вставляем товары в корзину с учетом типа операции и привязываем к cartOperationId
        cart.items.forEach { cartItem ->
            cartItemDao.insertCartItem(cartItem.toEntity(cartOperationId.toInt(), cartOperation.operationType))
        }
    }
}
