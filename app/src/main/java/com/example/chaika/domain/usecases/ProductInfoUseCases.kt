package com.example.chaika.domain.usecases

import com.example.chaika.data.data_source.ProductInfoDataSourceInterface
import com.example.chaika.data.local.LocalImageRepository
import com.example.chaika.data.room.repo.RoomProductInfoRepositoryInterface
import com.example.chaika.domain.models.ProductInfoDomain
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// Юзкейс для получения всех товаров из базы данных
class GetAllProductsUseCase @Inject constructor(
    private val roomProductInfoRepositoryInterface: RoomProductInfoRepositoryInterface // Используем интерфейс
) {
    operator fun invoke(): Flow<List<ProductInfoDomain>> {
        return roomProductInfoRepositoryInterface.getAllProducts()
    }
}

// Юзкейс для добавления товара в базу данных
class AddProductUseCase @Inject constructor(
    private val roomProductInfoRepositoryInterface: RoomProductInfoRepositoryInterface // Используем интерфейс
) {
    suspend operator fun invoke(productInfoDomain: ProductInfoDomain) {
        roomProductInfoRepositoryInterface.insertProduct(productInfoDomain)
    }
}

// Юзкейс для удаления товара из базы данных
class DeleteProductUseCase @Inject constructor(
    private val roomProductInfoRepositoryInterface: RoomProductInfoRepositoryInterface // Используем интерфейс
) {
    suspend operator fun invoke(productInfoDomain: ProductInfoDomain) {
        roomProductInfoRepositoryInterface.deleteProduct(productInfoDomain)
    }
}

class AddProductInfoUseCase @Inject constructor(
    private val productInfoRepository: RoomProductInfoRepositoryInterface,
    private val productInfoDataSource: ProductInfoDataSourceInterface,
    private val localImageRepository: LocalImageRepository
) {
    suspend operator fun invoke() {
        // Получаем список товаров с сервера (или из fake data source)
        val productInfoList = productInfoDataSource.fetchProductInfoList()

        // Сохраняем каждый товар в базе данных
        productInfoList.forEach { productInfo ->
            // Загружаем изображение и сохраняем его во внутренней памяти
            val imagePath = localImageRepository.saveImageFromUrl(
                productInfo.image,
                "${productInfo.name}.jpg"
            )

            // Создаём новый объект с путём к изображению
            val productWithImagePath = productInfo.copy(image = imagePath ?: productInfo.image)

            // Сохраняем товар в базе данных
            productInfoRepository.insertProduct(productWithImagePath)
        }
    }
}


//Тестовый юзкейс для предзаполнения БД товарами
class PrepopulateProductsUseCase @Inject constructor(
    private val roomProductInfoRepositoryInterface: RoomProductInfoRepositoryInterface
) {
    suspend operator fun invoke() {
        val products = listOf(
            ProductInfoDomain(
                id = 1,
                name = "Чай чёрный",
                description = "Классический чёрный чай",
                image = "black_tea.jpeg",
                price = 75.0
            ),
            ProductInfoDomain(
                id = 2,
                name = "Чай зелёный",
                description = "Зелёный чай с жасмином",
                image = "green_tea.png",
                price = 80.0
            ),
            ProductInfoDomain(
                id = 3,
                name = "Яблочный сок",
                description = "Свежий яблочный сок",
                image = "apple_juice.png",
                price = 90.0
            )
        )
        products.forEach { product ->
            roomProductInfoRepositoryInterface.insertProduct(product)
        }
    }
}