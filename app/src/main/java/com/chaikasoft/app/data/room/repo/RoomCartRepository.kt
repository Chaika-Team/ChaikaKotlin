package com.chaikasoft.app.data.room.repo

import androidx.room.withTransaction
import com.chaikasoft.app.data.room.AppDatabase
import com.chaikasoft.app.data.room.dao.CartItemDao
import com.chaikasoft.app.data.room.dao.CartOperationDao
import com.chaikasoft.app.data.room.mappers.toEntity
import com.chaikasoft.app.domain.models.CartDomain
import com.chaikasoft.app.domain.models.CartOperationDomain
import javax.inject.Inject

class RoomCartRepository @Inject constructor(
    private val db: AppDatabase,
    private val cartItemDao: CartItemDao,
    private val cartOperationDao: CartOperationDao
) : RoomCartRepositoryInterface {

    override suspend fun saveCartWithItemsAndOperation(
        cart: CartDomain,
        cartOperationDomain: CartOperationDomain
    ): Int = db.withTransaction {
        // Вставляем операцию и получаем её ID
        val cartOperationId = cartOperationDao.insertOperation(cartOperationDomain.toEntity())

        // Вставляем товары в корзину с учетом типа операции и привязываем к cartOperationId
        cart.items.forEach { cartItem ->
            cartItemDao.insertCartItem(
                cartItem.toEntity(
                    cartOperationId.toInt(),
                    cartOperationDomain.operationTypeDomain
                )
            )
        }
        // 3) Возвращаем ID
        cartOperationId.toInt()
    }
}
