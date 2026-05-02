package com.chaikasoft.app.data.local

import android.content.Context
import android.graphics.Bitmap
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.io.File
import java.nio.file.Files
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest

class LocalImageRepositoryTest : FunSpec({

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности.
     *
     * Описание:
     *   - Валидный bitmap (width/height > 0) должен сохраняться в images/<subDir>/<fileName>.
     *   - Метод должен вернуть абсолютный путь к сохраненному файлу.
     */
    test("saveImageFromUrl returns absolute path and creates file for valid bitmap") {
        runTest {
            val filesRoot = Files.createTempDirectory("local-image-test").toFile()
            val context = mockk<Context>()
            every { context.filesDir } returns filesRoot

            val bitmap = mockk<Bitmap>()
            every { bitmap.width } returns 100
            every { bitmap.height } returns 100
            every { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, any()) } returns true

            val repository = LocalImageRepository(
                context = context,
                bitmapLoader = { _, _ -> bitmap },
                ioDispatcher = Dispatchers.Unconfined,
            )

            val result = repository.saveImageFromUrl(
                imageUrl = "https://example.com/image.jpg",
                fileName = "avatar.jpg",
                subDir = "conductors",
            )

            result.shouldBe(File(filesRoot, "images/conductors/avatar.jpg").absolutePath)
            File(result!!).exists().shouldBeTrue()
        }
    }

    /**
     * Техника тест-дизайна: #2 Граничные значения.
     *
     * Описание:
     *   - Если bitmapLoader вернул null, метод должен завершиться без сохранения и вернуть null.
     */
    test("saveImageFromUrl returns null when bitmap loader returns null") {
        runTest {
            val filesRoot = Files.createTempDirectory("local-image-test").toFile()
            val context = mockk<Context>()
            every { context.filesDir } returns filesRoot

            val repository = LocalImageRepository(
                context = context,
                bitmapLoader = { _, _ -> null },
                ioDispatcher = Dispatchers.Unconfined,
            )

            repository.saveImageFromUrl("url", "x.jpg", "products").shouldBeNull()
        }
    }

    /**
     * Техника тест-дизайна: #2 Граничные значения.
     *
     * Описание:
     *   - Bitmap с нулевыми размерами считается невалидным.
     *   - Метод должен вернуть null и не записывать файл.
     */
    test("saveImageFromUrl returns null for bitmap with zero dimensions") {
        runTest {
            val filesRoot = Files.createTempDirectory("local-image-test").toFile()
            val context = mockk<Context>()
            every { context.filesDir } returns filesRoot

            val bitmap = mockk<Bitmap>()
            every { bitmap.width } returns 0
            every { bitmap.height } returns 10

            val repository = LocalImageRepository(
                context = context,
                bitmapLoader = { _, _ -> bitmap },
                ioDispatcher = Dispatchers.Unconfined,
            )

            repository.saveImageFromUrl("url", "x.jpg", "products").shouldBeNull()
            File(filesRoot, "images/products/x.jpg").exists().shouldBeFalse()
        }
    }

    /**
     * Техника тест-дизайна: #5 Error Guessing.
     *
     * Описание:
     *   - Любое исключение в процессе загрузки/сохранения изображения должно отрабатываться как null.
     */
    test("saveImageFromUrl returns null when bitmap loader throws exception") {
        runTest {
            val filesRoot = Files.createTempDirectory("local-image-test").toFile()
            val context = mockk<Context>()
            every { context.filesDir } returns filesRoot

            val repository = LocalImageRepository(
                context = context,
                bitmapLoader = { _, _ -> throw IllegalStateException("boom") },
                ioDispatcher = Dispatchers.Unconfined,
            )

            repository.saveImageFromUrl("url", "x.jpg", "products").shouldBeNull()
        }
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности.
     *
     * Описание:
     *   - При существующей директории должен вызываться deleteRecursively.
     *   - Метод возвращает true при штатном завершении.
     */
    test("deleteImagesInSubDir returns true and calls deleteRecursively for existing directory") {
        runTest {
            val filesRoot = Files.createTempDirectory("local-image-test").toFile()
            val target = File(filesRoot, "images/reports").apply { mkdirs() }
            File(target, "tmp.txt").writeText("1")

            val context = mockk<Context>()
            every { context.filesDir } returns filesRoot

            val repository = LocalImageRepository(
                context = context,
                bitmapLoader = { _, _ -> null },
                ioDispatcher = Dispatchers.Unconfined,
                deleteRecursively = { file ->
                    file shouldBe target
                    true
                },
            )

            repository.deleteImagesInSubDir("reports").shouldBeTrue()
        }
    }

    /**
     * Техника тест-дизайна: #2 Граничные значения.
     *
     * Описание:
     *   - Если поддиректория отсутствует, метод не должен падать и должен вернуть true.
     */
    test("deleteImagesInSubDir returns true when directory does not exist") {
        runTest {
            val filesRoot = Files.createTempDirectory("local-image-test").toFile()
            val context = mockk<Context>()
            every { context.filesDir } returns filesRoot

            val deleteRecursively = mockk<(File) -> Boolean>()
            every { deleteRecursively(any()) } returns true

            val repository = LocalImageRepository(
                context = context,
                bitmapLoader = { _, _ -> null },
                ioDispatcher = Dispatchers.Unconfined,
                deleteRecursively = deleteRecursively,
            )

            repository.deleteImagesInSubDir("missing").shouldBeTrue()
            io.mockk.verify(exactly = 0) { deleteRecursively(any()) }
        }
    }

    /**
     * Техника тест-дизайна: #5 Error Guessing.
     *
     * Описание:
     *   - Исключение в deleteRecursively должно быть поймано и преобразовано в false.
     */
    test("deleteImagesInSubDir returns false when deleteRecursively throws") {
        runTest {
            val filesRoot = Files.createTempDirectory("local-image-test").toFile()
            File(filesRoot, "images/faulty").mkdirs()

            val context = mockk<Context>()
            every { context.filesDir } returns filesRoot

            val repository = LocalImageRepository(
                context = context,
                bitmapLoader = { _, _ -> null },
                ioDispatcher = Dispatchers.Unconfined,
                deleteRecursively = { throw RuntimeException("delete failed") },
            )

            repository.deleteImagesInSubDir("faulty").shouldBeFalse()
        }
    }
})
