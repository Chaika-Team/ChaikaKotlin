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
        // Выполняем предзаполнение внутри runBlocking, чтобы дождаться завершения операций
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
            // В тесте с операциями вставляем их, а в тесте с пустым отчётом мы этого не будем делать.
            // Поэтому здесь можно вставлять операции только для тестGenerateTripReportUseCaseGeneratesAndSavesReport().
            // Для теста с пустым отчётом мы очистим таблицы операций.
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
            // Подготавливаем данные: вставляем операцию и элемент корзины.
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

            // Вызываем юзкейс генерации отчёта
            val result = generateTripReportUseCase()
            assertTrue("Отчёт должен быть успешно сгенерирован и сохранён", result)

            // Проверяем, что файл отчёта создан в директории "reports" в context.filesDir
            val reportsDir = File(appContext.filesDir, "reports")
            if (!reportsDir.exists()) {
                fail("Директория reports не существует")
            }
            val reportFiles = reportsDir.listFiles()
            // Проверяем, что есть файл с именем, начинающимся с "trip_report_"
            val reportFile = reportFiles?.firstOrNull { it.name.startsWith("trip_report_") }
            assertTrue("Отчёт должен иметь имя, начинающееся с 'trip_report_'", reportFile != null)

            try {
                val content = reportFile!!.readText(Charsets.UTF_8)
                assertTrue("Содержимое отчёта не должно быть пустым", content.isNotEmpty())
                // Проверяем, что содержимое содержит ключевые поля
                assertTrue("Отчёт должен содержать 'RouteID'", content.contains("RouteID"))
                assertTrue("Отчёт должен содержать 'StartTime'", content.contains("StartTime"))
                assertTrue("Отчёт должен содержать 'Carts'", content.contains("Carts"))
            } finally {
                // Гарантированно удаляем файл, даже если проверки не пройдены
                reportFile?.delete()
            }
        }
    }

    @Test
    fun testGenerateTripReportUseCaseGeneratesEmptyReport() {
        runBlocking {
            // Для симуляции пустого отчёта очищаем таблицы операций и корзины,
            // оставляя только проводника и товар.
            appDatabase.clearAllTables()
            // Вставляем проводника и товар (минимальные данные)
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
            productInfoDao.insertProduct(
                ProductInfo(
                    id = 101,
                    name = "Test Product",
                    description = "Test Product Description",
                    image = "https://example.com/product101.jpg",
                    price = 99.99
                )
            )
            // Не вставляем ни одной операции, поэтому отчет должен содержать пустой список "Carts".

            // Вызываем юзкейс генерации отчёта
            val result = generateTripReportUseCase()
            assertTrue("Отчёт должен быть успешно сгенерирован и сохранён", result)

            // Проверяем, что файл отчёта создан
            val reportsDir = File(appContext.filesDir, "reports")
            if (!reportsDir.exists()) {
                fail("Директория reports не существует")
            }
            val reportFiles = reportsDir.listFiles()
            val reportFile = reportFiles?.firstOrNull { it.name.startsWith("trip_report_") }
            assertTrue("Отчёт должен иметь имя, начинающееся с 'trip_report_'", reportFile != null)

            try {
                val content = reportFile!!.readText(Charsets.UTF_8)
                assertTrue("Содержимое отчёта не должно быть пустым", content.isNotEmpty())
                // Проверяем, что отчёт содержит ключ "Carts"
                assertTrue("Отчёт должен содержать 'Carts'", content.contains("Carts"))
                // Проверяем, что в поле "Carts" пустой список
                assertTrue(
                    "Отчёт должен содержать пустой список в 'Carts'",
                    content.contains("\"Carts\":[]")
                )
            } finally {
                reportFile?.delete()
            }
        }
    }
}
