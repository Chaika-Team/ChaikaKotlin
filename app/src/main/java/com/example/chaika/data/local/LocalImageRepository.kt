package com.example.chaika.data.local

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class LocalImageRepository @Inject constructor(
    private val context: Context,
) : LocalImageRepositoryInterface {

    /**
     * Сохраняет изображение из URL в локальную директорию.
     * @param imageUrl URL изображения для загрузки.
     * @param fileName Имя файла.
     * @param subDir Поддиректория (например, "products" или "conductors").
     * @return Абсолютный путь сохранённого файла или null в случае ошибки.
     */
    override suspend fun saveImageFromUrl(
        imageUrl: String,
        fileName: String,
        subDir: String,
    ): String? {
        return try {
            val bitmap = withContext(Dispatchers.IO) {
                Glide.with(context)
                    .asBitmap()
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .submit()
                    .get()
            }

            if (bitmap == null || bitmap.width <= 0 || bitmap.height <= 0) {
                Log.e("LocalImageRepository", "Invalid bitmap.")
                return null
            }

            val imageDir = File(context.filesDir, "images/$subDir")
            if (!imageDir.exists()) {
                imageDir.mkdirs()
            }

            val file = File(imageDir, fileName)
            withContext(Dispatchers.IO) {
                FileOutputStream(file).use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                }
            }

            Log.d("LocalImageRepository", "Image saved: ${file.absolutePath}")
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("LocalImageRepository", "Failed to save image: ${e.message}")
            null
        }
    }

    override suspend fun deleteImagesInSubDir(subDir: String): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val targetDir = File(context.filesDir, "images/$subDir")
                if (targetDir.exists()) {
                    val deleted = targetDir.deleteRecursively()
                    Log.d(
                        "LocalImageRepository",
                        "Deleted images from: ${targetDir.absolutePath}, result: $deleted",
                    )
                } else {
                    Log.d(
                        "LocalImageRepository",
                        "Directory does not exist: ${targetDir.absolutePath}",
                    )
                }
                true
            } catch (e: Exception) {
                Log.e(
                    "LocalImageRepository",
                    "Failed to delete images in subDir '$subDir': ${e.message}",
                )
                false
            }
        }
}
