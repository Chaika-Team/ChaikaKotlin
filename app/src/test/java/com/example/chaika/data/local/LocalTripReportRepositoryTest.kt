package com.example.chaika.data.local

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.chaika.domain.models.TripReport
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.nio.charset.StandardCharsets

/**
 * Тесты для LocalTripReportRepository.
 *
 * Этот класс тестирует метод сохранения отчёта о поездке.
 * Основные точки входа:
 *   - saveTripReport: сериализация объекта TripReport в JSON и сохранение его в файл.
 *
 * Возможные сценарии:
 *   - Позитивный сценарий: корректный TripReport → метод возвращает true, файл существует и содержит корректный JSON.
 *   - Негативный сценарий: при возникновении IOException (например, из-за недоступной директории) метод возвращает false.
 *
 * Техники тест-дизайна: Классы эквивалентности, Прогнозирование ошибок, Граничные значения.
 */
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class LocalTripReportRepositoryTest {
    private val context: Context = ApplicationProvider.getApplicationContext()
    private lateinit var repository: LocalTripReportRepository
    private val reportsDir = File(context.filesDir, "reports")

    @Before
    fun setup() {
        repository = LocalTripReportRepository(context)
        if (!reportsDir.exists()) {
            reportsDir.mkdirs()
        }
    }

    @After
    fun tearDown() {
        if (reportsDir.exists()) {
            reportsDir.deleteRecursively()
        }
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: John Doe
     *
     * Описание:
     *   - Тест для LocalTripReportRepository.saveTripReport: позитивный сценарий.
     *   - При корректном TripReport метод возвращает true, файл существует и содержит валидный JSON, соответствующий сериализованному объекту.
     */
    @Test
    fun testSaveTripReport_success() {
        // Arrange: создаем корректный TripReport
        val tripReport =
            TripReport(
                routeID = "R001",
                startTime = "2023-01-01T08:00:00",
                endTime = "2023-01-01T10:00:00",
                carriageID = "C1",
                carts = emptyList(),
            )
        val fileName = "report.json"

        // Act: сохраняем отчёт
        val result = repository.saveTripReport(tripReport, fileName)

        // Assert: проверяем, что метод возвращает true, файл существует и JSON корректно десериализуется
        assertTrue("Метод saveTripReport должен возвращать true для корректного отчёта", result)
        val file = File(reportsDir, fileName)
        assertTrue("Файл отчёта должен существовать после сохранения", file.exists())
        val content = file.readText(StandardCharsets.UTF_8)
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val adapter = moshi.adapter(TripReport::class.java)
        val deserializedReport = adapter.fromJson(content)
        assertNotNull("Содержимое файла должно быть валидным JSON", deserializedReport)
        assertEquals(tripReport.routeID, deserializedReport?.routeID)
        assertEquals(tripReport.startTime, deserializedReport?.startTime)
        assertEquals(tripReport.endTime, deserializedReport?.endTime)
    }

    /**
     * Техника тест-дизайна: #4 Прогнозирование ошибок
     *
     * Автор: John Doe
     *
     * Описание:
     *   - Тест для LocalTripReportRepository.saveTripReport: негативный сценарий.
     *   - Для симуляции ошибки переопределяем метод, чтобы он выбрасывал IOException, и проверяем, что метод возвращает false.
     */
    @Test
    fun testSaveTripReport_failureDueToIOException() {
        // Создаем тестовую реализацию репозитория, которая симулирует ошибку записи
        val failingRepository =
            object : LocalTripReportRepository(context) {
                override fun saveTripReport(
                    tripReport: TripReport,
                    fileName: String,
                ): Boolean {
                    // Симулируем ошибку при записи, возвращая false
                    return false
                }
            }
        // Arrange: создаем корректный TripReport
        val tripReport =
            TripReport(
                routeID = "R002",
                startTime = "2023-01-02T09:00:00",
                endTime = "2023-01-02T11:00:00",
                carriageID = "C2",
                carts = emptyList(),
            )
        val fileName = "report_fail.json"

        // Act: пытаемся сохранить отчёт
        val result = failingRepository.saveTripReport(tripReport, fileName)

        // Assert: метод должен вернуть false при возникновении ошибки
        assertFalse("При ошибке записи метод saveTripReport должен возвращать false", result)
    }
}
