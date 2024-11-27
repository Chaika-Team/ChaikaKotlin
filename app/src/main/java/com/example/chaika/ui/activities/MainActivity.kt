package com.example.chaika.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.chaika.domain.models.*
import com.example.chaika.domain.usecases.AddConductorUseCase
import com.example.chaika.domain.usecases.AddProductInfoUseCase
import com.example.chaika.domain.usecases.GenerateTripReportUseCase
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
    lateinit var generateTripReportUseCase: GenerateTripReportUseCase

    @Inject
    lateinit var inMemoryCartRepository: InMemoryCartRepositoryInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                    id = 0,
                    name = "Test Conductor",
                    employeeID = "t4e0s4t",
                    image = "https://example.com/image.jpg"
                )
                addConductorUseCase(conductor)
                println("Conductor added successfully.")

                // 3. ID проводника
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
                productItems.forEach { item -> inMemoryCartRepository.addItemToCart(item) }
                println("Products added to in-memory cart successfully.")

                val cartOperationAdd = CartOperationDomain(
                    operationTypeDomain = OperationTypeDomain.ADD,
                    conductorId = conductorId
                )
                saveCartWithItemsAndOperationUseCase(cartOperationAdd)
                println("Products added to package successfully with operation.")

                // 5. Подготовка товаров для продажи
                val saleItems = listOf(
                    CartItemDomain(
                        product = ProductInfoDomain(
                            id = 1,
                            name = "Product 1",
                            description = "Description for Product 1",
                            image = "path/to/image1.jpg",
                            price = 10.0
                        ),
                        quantity = 2
                    ),
                    CartItemDomain(
                        product = ProductInfoDomain(
                            id = 2,
                            name = "Product 2",
                            description = "Description for Product 2",
                            image = "path/to/image2.jpg",
                            price = 15.0
                        ),
                        quantity = 3
                    )
                )

                // Создаём отдельные операции для каждого товара
                saleItems.forEach { item ->
                    inMemoryCartRepository.addItemToCart(item)
                    val cartOperationSale = CartOperationDomain(
                        operationTypeDomain = OperationTypeDomain.SOLD_CASH,
                        conductorId = conductorId
                    )
                    saveCartWithItemsAndOperationUseCase(cartOperationSale)
                    println("Product sold successfully with operation: ${item.product.name}")
                }

                // 6. Генерация отчёта
                val reportResult = generateTripReportUseCase()
                if (reportResult) {
                    println("Trip report generated successfully.")
                } else {
                    println("Failed to generate trip report.")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                println("Failed to load fake data: ${e.message}")
            }
        }
    }
}
