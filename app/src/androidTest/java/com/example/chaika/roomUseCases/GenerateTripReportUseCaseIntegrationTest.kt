package com.example.chaika.roomUseCases

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.chaika.data.room.AppDatabase
import com.example.chaika.data.room.dao.CartItemDao
import com.example.chaika.data.room.dao.CartOperationDao
import com.example.chaika.data.room.dao.ConductorDao
import com.example.chaika.data.room.dao.ProductInfoDao
import com.example.chaika.data.room.entities.CartOperation
import com.example.chaika.data.room.entities.Conductor
import com.example.chaika.data.room.entities.ProductInfo
import com.example.chaika.domain.usecases.GenerateTripReportUseCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.Rule
import org.junit.runner.RunWith
import java.io.File
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class GenerateTripReportUseCaseIntegrationTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    // Инжектируем юзкейс для генерации отчёта поездки
    @Inject
    lateinit var generateTripReportUseCase: GenerateTripReportUseCase

    // База данных и DAO для предзаполнения и проверки данных
    @Inject
    lateinit var appDatabase: AppDatabase

    @Inject
    lateinit var cartOperationDao: CartOperationDao

    @Inject
    lateinit var cartItemDao: CartItemDao

    @Inject
    lateinit var conductorDao: ConductorDao

    @Inject
    lateinit var productInfoDao: ProductInfoDao

    private lateinit var appContext: Context

    @Before
    fun setup() {
        hiltRule.inject()
        appContext = ApplicationProvider.getApplicationContext()
        // Выполняем предзаполнение внутри runBlocking
        runBlocking {
            appDatabase.clearAllTables()
            // Вставляем проводника с id = 1 и employeeID "E123"
            conductorDao.insertConductor(
                Conductor(
                    id = 1,
                    name = "John",
                    familyName = "Doe",
                    givenName = "John",
                    employeeID = "E123",
                    image = "https://example.com/conductor.jpg"
                )
            )
            // Вставляем товар с id = 101
            productInfoDao.insertProduct(
                ProductInfo(
                    id = 101,
                    name = "Test Product",
                    description = "Test Product Description",
                    image = "https://example.com/product101.jpg",
                    price = 99.99
                )
            )
            // Вставляем операцию с conductorId = 1.
            val operationId = cartOperationDao.insertOperation(
                CartOperation(
                    id = 0, // автоинкремент
                    operationType = 0, // например, 0 соответствует ADD
                    operationTime = "2024-10-15T00:12:00+03:00",
                    conductorId = 1
                )
            ).toInt()
            // Вставляем элемент корзины для этой операции, ссылаясь на товар с id = 101.
            cartItemDao.insertCartItem(
                com.example.chaika.data.room.entities.CartItem(
                    id = 0,
                    cartOperationId = operationId,
                    productId = 101,
                    impact = 2
                )
            )
        }
    }

    @After
    fun tearDown() {
        // Закрываем базу после теста
        appDatabase.close()
    }

    @Test
    fun testGenerateTripReportUseCaseGeneratesAndSavesReport() {
        runBlocking {
            // Вызываем юзкейс генерации отчёта
            val result = generateTripReportUseCase()
            assertTrue("Отчёт должен быть успешно сгенерирован и сохранён", result)

            // Проверяем, что файл отчёта создан в директории "reports" в context.filesDir
            val reportsDir = File(appContext.filesDir, "reports")
            if (!reportsDir.exists()) {
                fail("Директория reports не существует")
            }
            val reportFiles = reportsDir.listFiles()
            // Используем безопасный вызов, чтобы избежать предупреждения
            val reportFile = reportFiles?.firstOrNull { it.name.startsWith("trip_report_") }
            assertTrue("Отчёт должен иметь имя, начинающееся с 'trip_report_'", reportFile != null)

            // Читаем содержимое файла и проводим проверки
            val content = reportFile?.readText(Charsets.UTF_8)
            assertTrue("Содержимое отчёта не должно быть пустым", !content.isNullOrEmpty())
            // Проверяем, что содержимое содержит ключевые поля, например "RouteID", "StartTime", "Carts"
            assertTrue("Отчёт должен содержать 'RouteID'", content!!.contains("RouteID"))
            assertTrue("Отчёт должен содержать 'StartTime'", content.contains("StartTime"))
            assertTrue("Отчёт должен содержать 'Carts'", content.contains("Carts"))

            // Удаляем созданный файл, чтобы не оставлять мусор
            reportFile.delete()
        }
    }
}
