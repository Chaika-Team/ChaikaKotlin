@file:OptIn(ExperimentalStdlibApi::class)

package com.example.chaika.data.local

import android.content.Context
import android.graphics.Bitmap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.io.File

class LocalImageRepositoryTest {
    @TempDir
    lateinit var tempDir: File

    /**
     * Создает мок объекта Context с использованием Mockito-inline для мокирования final-метода getFilesDir().
     */
    private fun createMockContext(): Context {
        val context: Context = mock()
        whenever(context.filesDir).thenReturn(tempDir)
        return context
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности, #2 Граничные значения
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для метода saveImageFromUrl: сценарий, когда Bitmap имеет валидные размеры (ширина и высота > 0).
     *   - Ожидается, что изображение будет сохранено, и метод вернет абсолютный путь к файлу.
     */
    @Test
    fun `saveImageFromUrl returns file path when bitmap is valid`() =
        runTest {
            val context = createMockContext()
            val fakeBitmap: Bitmap = mock()
            whenever(fakeBitmap.width).thenReturn(100)
            whenever(fakeBitmap.height).thenReturn(100)
            // Все аргументы передаются через матчеры: eq() и any()
            whenever(
                fakeBitmap.compress(
                    eq(Bitmap.CompressFormat.JPEG),
                    eq(100),
                    any(),
                ),
            ).thenReturn(
                true,
            )

            val fakeBitmapLoader: suspend (Context, String) -> Bitmap? = { _, _ -> fakeBitmap }
            val dispatcher: CoroutineDispatcher = coroutineContext[CoroutineDispatcher]!!
            val repository = LocalImageRepository(context, fakeBitmapLoader, dispatcher)

            val result =
                repository.saveImageFromUrl("http://example.com/image.jpg", "test.jpg", "products")
            assertNotNull(result)
            val savedFile = File(result!!)
            assertTrue(savedFile.exists())
        }

    /**
     * Техника тест-дизайна: #5 Прогнозирование ошибок
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для метода saveImageFromUrl: сценарий, когда функция загрузки Bitmap возвращает null.
     *   - Ожидается, что метод корректно обработает ситуацию и вернет null.
     */
    @Test
    fun `saveImageFromUrl returns null when bitmap is null`() =
        runTest {
            val context = createMockContext()
            val fakeBitmapLoader: suspend (Context, String) -> Bitmap? = { _, _ -> null }
            val dispatcher: CoroutineDispatcher = coroutineContext[CoroutineDispatcher]!!
            val repository = LocalImageRepository(context, fakeBitmapLoader, dispatcher)

            val result =
                repository.saveImageFromUrl("http://example.com/image.jpg", "test.jpg", "products")
            assertNull(result)
        }

    /**
     * Техника тест-дизайна: #2 Граничные значения
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для метода saveImageFromUrl: сценарий, когда Bitmap имеет нулевые размеры (ширина или высота равны 0).
     *   - Ожидается, что метод не сохранит изображение и вернет null.
     */
    @Test
    fun `saveImageFromUrl returns null when bitmap has zero dimensions`() =
        runTest {
            val context = createMockContext()
            val fakeBitmap: Bitmap = mock()
            whenever(fakeBitmap.width).thenReturn(0)
            whenever(fakeBitmap.height).thenReturn(0)

            val fakeBitmapLoader: suspend (Context, String) -> Bitmap? = { _, _ -> fakeBitmap }
            val dispatcher: CoroutineDispatcher = coroutineContext[CoroutineDispatcher]!!
            val repository = LocalImageRepository(context, fakeBitmapLoader, dispatcher)

            val result =
                repository.saveImageFromUrl("http://example.com/image.jpg", "test.jpg", "products")
            assertNull(result)
        }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для метода deleteImagesInSubDir: сценарий, когда целевая директория существует и содержит файлы.
     *   - Ожидается, что метод удалит директорию и вернет true.
     */
    @Test
    fun `deleteImagesInSubDir returns true when directory exists and is deleted`() =
        runTest {
            val context = createMockContext()
            // Создаем тестовую директорию и файл внутри неё.
            val imagesDir = File(tempDir, "images/products")
            imagesDir.mkdirs()
            File(imagesDir, "dummy.txt").writeText("dummy")

            val fakeBitmapLoader: suspend (Context, String) -> Bitmap? = { _, _ -> null }
            val dispatcher: CoroutineDispatcher = coroutineContext[CoroutineDispatcher]!!
            val repository = LocalImageRepository(context, fakeBitmapLoader, dispatcher)

            val result = repository.deleteImagesInSubDir("products")
            assertTrue(result)
            assertFalse(imagesDir.exists())
        }

    /**
     * Техника тест-дизайна: #2 Граничные значения
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для метода deleteImagesInSubDir: сценарий, когда целевая директория не существует.
     *   - Ожидается, что метод корректно обработает ситуацию и вернет true.
     */
    @Test
    fun `deleteImagesInSubDir returns true when directory does not exist`() =
        runTest {
            val context = createMockContext()
            val imagesDir = File(tempDir, "images/nonexistent")
            if (imagesDir.exists()) imagesDir.deleteRecursively()

            val fakeBitmapLoader: suspend (Context, String) -> Bitmap? = { _, _ -> null }
            val dispatcher: CoroutineDispatcher = coroutineContext[CoroutineDispatcher]!!
            val repository = LocalImageRepository(context, fakeBitmapLoader, dispatcher)

            val result = repository.deleteImagesInSubDir("nonexistent")
            assertTrue(result)
        }

    /**
     * Техника тест-дизайна: #5 Прогнозирование ошибок
     *
     * Автор: Кулаков Никита
     *
     * Описание:
     *   - Тест для метода deleteImagesInSubDir: сценарий, когда функция удаления (deleteRecursively) выбрасывает исключение.
     *   - Ожидается, что метод обработает исключение и вернет false.
     */
    @Test
    fun `deleteImagesInSubDir returns false when exception occurs during deletion`() =
        runTest {
            val context = createMockContext()
            // Создаем тестовую директорию "images/faulty" чтобы deleteRecursively была вызвана.
            val faultyDir = File(tempDir, "images/faulty")
            faultyDir.mkdirs()

            val fakeBitmapLoader: suspend (Context, String) -> Bitmap? = { _, _ -> null }
            val dispatcher: CoroutineDispatcher = coroutineContext[CoroutineDispatcher]!!
            // Передаем lambda, которая всегда бросает исключение.
            val repository =
                LocalImageRepository(
                    context,
                    fakeBitmapLoader,
                    dispatcher,
                    deleteRecursively = { throw Exception("Simulated deletion exception") },
                )

            val result = repository.deleteImagesInSubDir("faulty")
            assertFalse(result)
        }
}
