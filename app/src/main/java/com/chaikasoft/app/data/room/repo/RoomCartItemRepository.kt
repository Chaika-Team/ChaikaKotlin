package com.chaikasoft.app.data.room.repo

import com.chaikasoft.app.data.room.dao.CartItemDao
import com.chaikasoft.app.data.room.dao.ProductInfoDao
import com.chaikasoft.app.data.room.mappers.toReport
import com.chaikasoft.app.domain.models.report.CartItemReport
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomCartItemRepository @Inject constructor(
    private val cartItemDao: CartItemDao,
    private val productInfoDao: ProductInfoDao
) : RoomCartItemRepositoryInterface {

    override fun getCartItemReportsByOperationId(operationId: Int): Flow<List<CartItemReport>> =
        cartItemDao.getCartItemsByCartOpId(operationId).map { cartItems ->
            cartItems.map { cartItem ->
                val productInfo = productInfoDao.getProductById(cartItem.productId)
                    ?: throw IllegalArgumentException(
                        "Product not found for ID: ${cartItem.productId}"
                    )
                cartItem.toReport(productInfo)
            }
        }
}
