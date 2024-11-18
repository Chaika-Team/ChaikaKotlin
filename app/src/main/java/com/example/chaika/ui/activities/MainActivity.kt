package com.example.chaika.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.chaika.domain.models.CartItemDomain
import com.example.chaika.domain.models.CartOperationDomain
import com.example.chaika.domain.models.ConductorDomain
import com.example.chaika.domain.models.OperationTypeDomain
import com.example.chaika.domain.models.ProductInfoDomain
import com.example.chaika.domain.usecases.AddConductorUseCase
import com.example.chaika.domain.usecases.AddProductInfoUseCase
import com.example.chaika.domain.usecases.SaveCartWithItemsAndOperationUseCase
import com.example.chaika.data.inMemory.InMemoryCartRepositoryInterface
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var addProductInfoUseCase: AddProductInfoUseCase

    @Inject
    lateinit var addConductorUseCase: AddConductorUseCase

    @Inject
    lateinit var saveCartWithItemsAndOperationUseCase: SaveCartWithItemsAndOperationUseCase

    @Inject
    lateinit var inMemoryCartRepository: InMemoryCartRepositoryInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Выполняем фейковую загрузку данных при старте активности
        performFakeDataLoading()
    }

    private fun performFakeDataLoading() {
        lifecycleScope.launch {
            try {
                // 1. Загрузка фейковых данных продуктов
                addProductInfoUseCase()
                println("Fake product data loaded successfully.")

                // 2. Создание проводника
                val conductor = ConductorDomain(
                    id = 0,  // id будет автоматически присвоен
                    name = "Test Conductor",
                    image = "https://example.com/image.jpg"
                )
                addConductorUseCase(conductor)
                println("Conductor added successfully.")

                // 3. Предполагаем, что ID созданного проводника — 1
                val conductorId = 1

                // 4. Добавление товаров в корзину для операции `ADD`
                val productItems = listOf(
                    CartItemDomain(
                        product = ProductInfoDomain(
                            id = 1,
                            name = "Product 1",
                            description = "Description for Product 1",
                            image = "path/to/image1.jpg",
                            price = 10.0
                        ),
                        quantity = 10
                    ),
                    CartItemDomain(
                        product = ProductInfoDomain(
                            id = 2,
                            name = "Product 2",
                            description = "Description for Product 2",
                            image = "path/to/image2.jpg",
                            price = 15.0
                        ),
                        quantity = 5
                    )
                )

                // Добавляем каждый товар в корзину через InMemoryCartRepository
                productItems.forEach { item ->
                    inMemoryCartRepository.addItemToCart(item)
                }
                println("Products added to in-memory cart successfully.")

                // 5. Создание операции `ADD` и сохранение корзины
                val cartOperationAdd = CartOperationDomain(
                    operationTypeDomain = OperationTypeDomain.ADD,
                    conductorId = conductorId
                )

                saveCartWithItemsAndOperationUseCase(cartOperationAdd)
                println("Products added to package successfully with operation.")

                // 6. Подготовка товаров для продажи
                val saleItems = listOf(
                    CartItemDomain(
                        product = ProductInfoDomain(
                            id = 1,
                            name = "Product 1",
                            description = "Description for Product 1",
                            image = "path/to/image1.jpg",
                            price = 10.0
                        ),
                        quantity = 2  // Продадим 2 единицы
                    ),
                    CartItemDomain(
                        product = ProductInfoDomain(
                            id = 2,
                            name = "Product 2",
                            description = "Description for Product 2",
                            image = "path/to/image2.jpg",
                            price = 15.0
                        ),
                        quantity = 3  // Продадим 3 единицы
                    )
                )

                // Добавляем каждый товар в корзину для операции `SOLD_CASH`
                saleItems.forEach { item ->
                    inMemoryCartRepository.addItemToCart(item)
                }
                println("Products added to in-memory cart for sale successfully.")

                // 7. Создание операции `SOLD_CASH` и сохранение корзины
                val cartOperationSale = CartOperationDomain(
                    operationTypeDomain = OperationTypeDomain.SOLD_CASH,
                    conductorId = conductorId
                )

                saveCartWithItemsAndOperationUseCase(cartOperationSale)
                println("Products sold successfully with operation.")

            } catch (e: Exception) {
                e.printStackTrace()
                println("Failed to load fake data: ${e.message}")
            }
        }
    }
}
