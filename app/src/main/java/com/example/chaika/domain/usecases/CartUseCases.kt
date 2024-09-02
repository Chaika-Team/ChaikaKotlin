package com.example.chaika.domain.usecases

import com.example.chaika.data.room.repo.CartRepository
import com.example.chaika.domain.models.Cart
import com.example.chaika.domain.models.CartOperation
import javax.inject.Inject

class SaveCartWithItemsAndOperationUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(cart: Cart, cartOperation: CartOperation) {
        // Сохраняем корзину и операции
        cartRepository.saveCartWithItemsAndOperation(cart, cartOperation)
    }
}
