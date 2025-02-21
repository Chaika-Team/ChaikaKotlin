package com.example.chaika.domain.usecases

import com.example.chaika.data.dataSource.ProductInfoDataSourceInterface
import com.example.chaika.data.local.ImageSubDir
import com.example.chaika.data.local.LocalImageRepositoryInterface
import com.example.chaika.data.room.repo.RoomProductInfoRepositoryInterface
import com.example.chaika.domain.models.ProductInfoDomain
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// Юзкейс для получения всех товаров из базы данных
class GetAllProductsUseCase @Inject constructor(
    private val roomProductInfoRepositoryInterface: RoomProductInfoRepositoryInterface, // Используем интерфейс
) {
    operator fun invoke(): Flow<List<ProductInfoDomain>> {
        return roomProductInfoRepositoryInterface.getAllProducts()
    }
}

// Юзкейс для добавления товара в базу данных
class AddProductUseCase @Inject constructor(
    private val roomProductInfoRepositoryInterface: RoomProductInfoRepositoryInterface, // Используем интерфейс
) {
    suspend operator fun invoke(productInfoDomain: ProductInfoDomain) {
        roomProductInfoRepositoryInterface.insertProduct(productInfoDomain)
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
class AddProductInfoUseCase @Inject constructor(
    private val productInfoRepository: RoomProductInfoRepositoryInterface,
    private val productInfoDataSource: ProductInfoDataSourceInterface,
    private val localImageRepository: LocalImageRepositoryInterface,
) {
    suspend operator fun invoke() {
        // Получаем список товаров с сервера (или из fake data source)
        val productInfoList = productInfoDataSource.fetchProductInfoList()

        // Сохраняем каждый товар в базе данных
        productInfoList.forEach { productInfo ->
            // Загружаем изображение и сохраняем его во внутренней памяти
            val imagePath = localImageRepository.saveImageFromUrl(
                imageUrl = productInfo.image,
                fileName = "${productInfo.name}.jpg",
                subDir = ImageSubDir.PRODUCTS.folder,
            )

            // Создаём новый объект с путём к изображению
            val productWithImagePath = productInfo.copy(image = imagePath ?: productInfo.image)

            // Сохраняем товар в базе данных
            productInfoRepository.insertProduct(productWithImagePath)
        }
    }
}
