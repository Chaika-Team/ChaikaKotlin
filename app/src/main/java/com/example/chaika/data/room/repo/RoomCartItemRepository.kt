package com.example.chaika.data.room.repo

import com.example.chaika.data.room.dao.CartItemDao
import com.example.chaika.data.room.dao.ProductInfoDao
import com.example.chaika.data.room.mappers.toReport
import com.example.chaika.domain.models.report.CartItemReport
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomCartItemRepository @Inject constructor(
    private val cartItemDao: CartItemDao,
    private val productInfoDao: ProductInfoDao,
) : RoomCartItemRepositoryInterface {

    override fun getCartItemReportsByOperationId(operationId: Int): Flow<List<CartItemReport>> {
        return cartItemDao.getCartItemsByCartOpId(operationId).map { cartItems ->
            cartItems.map { cartItem ->
                val productInfo = productInfoDao.getProductById(cartItem.productId)
                    ?: throw IllegalArgumentException("Product not found for ID: ${cartItem.productId}")
                cartItem.toReport(productInfo)
            }
        }
    }
}
