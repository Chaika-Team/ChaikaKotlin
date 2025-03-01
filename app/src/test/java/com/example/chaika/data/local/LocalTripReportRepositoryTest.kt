package com.example.chaika.data.local

import android.content.Context
import com.example.chaika.domain.models.CartItemReport
import com.example.chaika.domain.models.CartOperationReport
import com.example.chaika.domain.models.TripReport
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets

class LocalTripReportRepositoryTest {
    @TempDir
    lateinit var tempDir: File

    /**
     * Создает мок Context с использованием Mockito-inline для мокирования final-метода getFilesDir().
     */
    private fun createMockContext(): Context {
        val context: Context = mock()
        whenever(context.filesDir).thenReturn(tempDir)
        return context
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для метода saveTripReport: сценарий, когда передан валидный объект TripReport.
     *   - Ожидается, что отчет будет успешно сериализован и сохранен в файл, а метод вернет true.
     */
    @Test
    fun `saveTripReport returns true for valid TripReport`() {
        val context = createMockContext()
        val tripReport =
            TripReport(
                routeID = "1",
                startTime = "2021-10-01T12:00:00",
                endTime = "2021-10-01T13:00:00",
                carriageID = "1",
                carts =
                    listOf(
                        CartOperationReport(
                            employeeID = "1",
                            operationType = 1,
                            operationTime = "2021-10-01T12:30:00",
                            items =
                                listOf(
                                    CartItemReport(
                                        productID = 1,
                                        quantity = 2,
                                        price = 100.0,
                                    ),
                                ),
                        ),
                    ),
            )
        val repository = LocalTripReportRepository(context)
        val fileName = "report.json"

        val result = repository.saveTripReport(tripReport, fileName)
        assertTrue(result)
        val reportFile = File(tempDir, "reports/$fileName")
        assertTrue(reportFile.exists())
        val content = reportFile.readText(StandardCharsets.UTF_8)
        // Проверяем, что JSON содержит ключи, как указано в аннотациях
        assertTrue(content.contains("\"RouteID\":\"1\""))
        assertTrue(content.contains("\"StartTime\":\"2021-10-01T12:00:00\""))
    }

    /**
     * Техника тест-дизайна: #5 Прогнозирование ошибок
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для метода saveTripReport: сценарий, когда при записи файла выбрасывается IOException.
     *   - Для симуляции ошибки используется MockK для замокирования конструктора FileOutputStream.
     *   - Ожидается, что метод вернет false.
     */
    @Test
    fun `saveTripReport returns false when IOException occurs during file writing`() {
        val context = createMockContext()
        // Мокаем FileOutputStream с использованием MockK для final-класса.
        io.mockk.mockkConstructor(FileOutputStream::class)
        io.mockk.every { anyConstructed<FileOutputStream>().write(ofType(ByteArray::class)) } throws
            IOException("Simulated IO Exception")
        val repository = LocalTripReportRepository(context)
        val tripReport =
            TripReport(
                routeID = "1",
                startTime = "2021-10-01T12:00:00",
                endTime = "2021-10-01T13:00:00",
                carriageID = "1",
                carts =
                    listOf(
                        CartOperationReport(
                            employeeID = "1",
                            operationType = 1,
                            operationTime = "2021-10-01T12:30:00",
                            items =
                                listOf(
                                    CartItemReport(
                                        productID = 1,
                                        quantity = 2,
                                        price = 100.0,
                                    ),
                                ),
                        ),
                    ),
            )
        val result = repository.saveTripReport(tripReport, "faulty.json")
        assertFalse(result)
        io.mockk.unmockkConstructor(FileOutputStream::class)
    }
}
