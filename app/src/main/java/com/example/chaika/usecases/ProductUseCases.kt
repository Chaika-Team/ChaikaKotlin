package com.example.chaika.usecases

import com.example.chaika.dataBase.entities.Product
import com.example.chaika.dataBase.models.ActionRepository
import com.example.chaika.dataBase.models.ProductRepository
import com.example.chaika.utils.ProductInTrip
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// Use case для получения всех продуктов
class GetAllProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend fun execute(): Flow<List<Product>> {
        return productRepository.getAllProducts()
    }
}

// Use case для добавления продуктов в базу
class InsertProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend fun execute(products: List<Product>) {
        productRepository.insertAll(products)
    }
}

// Use case для добавления действия (например, покупка или продажа продукта)
class AddProductActionUseCase @Inject constructor(
    private val actionRepository: ActionRepository
) {
    suspend fun execute(tripId: Int, productId: Int, operationId: Int, count: Int) {
        actionRepository.addAction(tripId, productId, operationId, count)
    }
}

// Use case для фильтрации продуктов по запросу пользователя
class FilterProductsUseCase @Inject constructor() {
    fun execute(products: List<Product>, query: String): List<Product> {
        return if (query.isEmpty()) {
            products
        } else {
            products.filter { product ->
                product.title.lowercase().contains(query.lowercase())
            }
        }
    }
}

//ProductTable
// Новый Use case для получения продуктов по идентификатору поездки
class GetProductsByTripUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend fun execute(tripId: Int): List<ProductInTrip> {
        return productRepository.getProductsByTrip(tripId)
    }
}

// Новый Use case для удаления действий для продукта в конкретной поездке
class DeleteActionsForProductInTripUseCase @Inject constructor(
    private val actionRepository: ActionRepository
) {
    suspend fun execute(productId: Int, tripId: Int) {
        actionRepository.deleteActionsForProductInTrip(productId, tripId)
    }
}
