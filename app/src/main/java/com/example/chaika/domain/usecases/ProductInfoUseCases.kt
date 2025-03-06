package com.example.chaika.domain.usecases

import com.example.chaika.data.dataSource.repo.ChaikaSoftApiServiceRepositoryInterface
import com.example.chaika.data.local.ImageSubDir
import com.example.chaika.data.local.LocalImageRepositoryInterface
import com.example.chaika.data.room.repo.RoomProductInfoRepositoryInterface
import com.example.chaika.domain.models.ProductInfoDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

// Юзкейс для получения всех товаров из базы данных
class GetAllProductsUseCase @Inject constructor(
    private val roomProductInfoRepositoryInterface: RoomProductInfoRepositoryInterface, // Используем интерфейс
) {
    operator fun invoke(): Flow<List<ProductInfoDomain>> {
        return roomProductInfoRepositoryInterface.getAllProducts()
    }
}

// Юзкейс для удаления товара из базы данных
class DeleteProductUseCase @Inject constructor(
    private val roomProductInfoRepositoryInterface: RoomProductInfoRepositoryInterface, // Используем интерфейс
) {
    suspend operator fun invoke(productInfoDomain: ProductInfoDomain) {
        roomProductInfoRepositoryInterface.deleteProduct(productInfoDomain)
    }
}

/**
 * Use case для получения списка товаров из ChaikaSoft.
 *
 * Обращается к ChaikaSoftApiServiceRepositoryInterface, который использует ChaikaSoftApiService для запроса.
 * По умолчанию возвращает до 100 товаров, начиная с offset = 0.
 */
class FetchProductsFromServerUseCase @Inject constructor(
    private val repository: ChaikaSoftApiServiceRepositoryInterface,
) {
    suspend operator fun invoke(limit: Int = 100, offset: Int = 0): List<ProductInfoDomain> =
        withContext(Dispatchers.IO) {
            repository.fetchProducts(limit, offset)
        }
}

/**
 * Use case для сохранения списка товаров локально.
 *
 * Для каждого товара:
 * 1. Скачивает изображение через LocalImageRepositoryInterface, сохраняя его в директорию [ImageSubDir.PRODUCTS.folder].
 * 2. Обновляет доменную модель [ProductInfoDomain], заменяя поле image на локальный путь (если удалось сохранить).
 * 3. Сохраняет обновлённый товар в базу данных через RoomProductInfoRepositoryInterface.
 */
class SaveProductsLocallyUseCase @Inject constructor(
    private val productInfoRepository: RoomProductInfoRepositoryInterface,
    private val localImageRepository: LocalImageRepositoryInterface,
) {
    suspend operator fun invoke(products: List<ProductInfoDomain>): List<ProductInfoDomain> =
        withContext(Dispatchers.IO) {
            products.forEach { product ->
                val imagePath = localImageRepository.saveImageFromUrl(
                    imageUrl = product.image,
                    fileName = "${product.name}.jpg",
                    subDir = ImageSubDir.PRODUCTS.folder
                )
                val productWithImagePath = product.copy(image = imagePath ?: product.image)
                productInfoRepository.insertProduct(productWithImagePath)
            }
            products
        }
}

/**
 * Объединённый use case для получения товаров с сервера и их сохранения локально.
 *
 * Сначала получает список товаров с сервера с помощью [FetchProductsFromServerUseCase],
 * а затем сохраняет их локально через [SaveProductsLocallyUseCase].
 */
class FetchAndSaveProductsUseCase @Inject constructor(
    private val fetchProductsFromServerUseCase: FetchProductsFromServerUseCase,
    private val saveProductsLocallyUseCase: SaveProductsLocallyUseCase
) {
    suspend operator fun invoke(limit: Int = 100, offset: Int = 0): List<ProductInfoDomain> {
        val products = fetchProductsFromServerUseCase(limit, offset)
        return saveProductsLocallyUseCase(products)
    }
}