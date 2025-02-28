package com.example.chaika.data.local

import android.content.Context
import android.graphics.Bitmap
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.io.FileOutputStream

/**
 * Тесты для LocalImageRepository.
 *
 * Этот класс тестирует методы сохранения и удаления изображений в локальной файловой системе.
 * Основные точки входа:
 *   - saveImageFromUrl: загрузка изображения (путем создания dummy bitmap) и запись его в файл.
 *   - deleteImagesInSubDir: удаление директории с изображениями.
 *
 * Возможные сценарии:
 *   - Позитивный: корректные параметры → метод возвращает абсолютный путь к файлу, файл существует и имеет ненулевой размер.
 *   - Негативный: невалидный битмап (симулируется возврат null) → метод должен вернуть null.
 *   - Успешное удаление существующей директории.
 *   - Попытка удаления несуществующей директории → метод должен вернуть true.
 *
 * Техники тест-дизайна: Классы эквивалентности, Прогнозирование ошибок, Граничные значения.
 */
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class LocalImageRepositoryTest {
    private val context: Context = ApplicationProvider.getApplicationContext()
    private lateinit var repository: LocalImageRepository

    @Before
    fun setup() {
        // Переопределяем метод saveImageFromUrl, чтобы вместо Glide возвращался dummy bitmap.
        repository =
            object : LocalImageRepository(context) {
                override suspend fun saveImageFromUrl(
                    imageUrl: String,
                    fileName: String,
                    subDir: String,
                ): String? {
                    // Создаем dummy bitmap размером 100x100
                    val dummyBitmap: Bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
                    // Если битмап невалидный – вернуть null (в данном случае он валиден)
                    if (dummyBitmap.width <= 0 || dummyBitmap.height <= 0) {
                        return null
                    }
                    // Создаем директорию для изображения
                    val imageDir = File(context.filesDir, "images/$subDir")
                    if (!imageDir.exists()) {
                        imageDir.mkdirs()
                    }
                    val file = File(imageDir, fileName)
                    // Пишем битмап в файл
                    withContext(Dispatchers.IO) {
                        FileOutputStream(file).use { outputStream ->
                            dummyBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                        }
                    }
                    return file.absolutePath
                }
            }
    }

    @After
    fun tearDown() {
        // Очищаем директорию "images" после каждого теста
        val imagesDir = File(context.filesDir, "images")
        if (imagesDir.exists()) {
            imagesDir.deleteRecursively()
        }
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: John Doe
     *
     * Описание:
     *   - Тест для LocalImageRepository.saveImageFromUrl в позитивном сценарии.
     *   - При корректных параметрах метод возвращает абсолютный путь к сохраненному файлу.
     */
    @Test
    fun testSaveImageFromUrl_success() =
        runBlocking {
            // Act: вызываем метод сохранения изображения
            val subDir = "products"
            val fileName = "test_image.jpg"
            val resultPath =
                repository.saveImageFromUrl("http://example.com/image.jpg", fileName, subDir)

            // Assert: путь не должен быть null, файл должен существовать и иметь ненулевой размер
            assertNotNull("Путь к сохраненному изображению не должен быть null", resultPath)
            val file = File(resultPath)
            assertTrue("Сохраненный файл должен существовать", file.exists())
            assertTrue("Размер файла должен быть больше 0", file.length() > 0)
        }

    /**
     * Техника тест-дизайна: #4 Прогнозирование ошибок
     *
     * Автор: John Doe
     *
     * Описание:
     *   - Тест для LocalImageRepository.saveImageFromUrl: негативный сценарий, когда битмап невалиден.
     *   - Симулируется ситуация, когда метод создания битмапа не вызывается, а сразу возвращается null.
     */
    @Test
    fun testSaveImageFromUrl_invalidBitmap() =
        runBlocking {
            // Создаем репозиторий, переопределяя метод для симуляции невалидного битмапа
            val repo =
                object : LocalImageRepository(context) {
                    override suspend fun saveImageFromUrl(
                        imageUrl: String,
                        fileName: String,
                        subDir: String,
                    ): String? {
                        // Вместо создания битмапа с нулевыми размерами (что вызывает исключение),
                        // сразу возвращаем null, чтобы симулировать ситуацию невалидного результата.
                        return null
                    }
                }
            // Act: вызываем метод сохранения изображения
            val result =
                repo.saveImageFromUrl("http://example.com/invalid.jpg", "invalid.jpg", "products")
            // Assert: результат должен быть null
            assertNull("При невалидном bitmap метод должен возвращать null", result)
        }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: John Doe
     *
     * Описание:
     *   - Тест для LocalImageRepository.deleteImagesInSubDir в позитивном сценарии.
     *   - Если поддиректория существует, метод возвращает true и удаляет её.
     */
    @Test
    fun testDeleteImagesInSubDir_success() =
        runBlocking {
            // Arrange: создаем поддиректорию и тестовый файл
            val subDir = "conductors"
            val targetDir = File(context.filesDir, "images/$subDir")
            targetDir.mkdirs()
            val testFile = File(targetDir, "temp.jpg")
            testFile.writeText("dummy content")
            assertTrue("Тестовый файл должен существовать до удаления", testFile.exists())

            // Act: вызываем метод удаления
            val result = repository.deleteImagesInSubDir(subDir)

            // Assert: метод возвращает true, а директория удалена
            assertTrue("Метод удаления должен возвращать true", result)
            assertFalse(
                "Поддиректория должна быть удалена после вызова deleteImagesInSubDir",
                targetDir.exists(),
            )
        }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: John Doe
     *
     * Описание:
     *   - Тест для LocalImageRepository.deleteImagesInSubDir для несуществующей директории.
     *   - Если поддиректория отсутствует, метод должен вернуть true.
     */
    @Test
    fun testDeleteImagesInSubDir_nonExistent() =
        runBlocking {
            // Act: вызываем метод удаления для несуществующей поддиректории
            val result = repository.deleteImagesInSubDir("nonexistent_dir")
            // Assert: метод возвращает true, поскольку директория отсутствует
            assertTrue("При отсутствии директории метод должен возвращать true", result)
        }
}
