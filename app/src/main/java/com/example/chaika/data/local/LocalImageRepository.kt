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
    private val context: Context
) {
    suspend fun saveImageFromUrl(imageUrl: String, fileName: String): String? {
        return try {
            // Загрузка изображения
            val bitmap = withContext(Dispatchers.IO) {
                Glide.with(context)
                    .asBitmap()
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .submit()
                    .get()
            }

            // Проверка bitmap
            if (bitmap == null || bitmap.width <= 0 || bitmap.height <= 0) {
                Log.e("LocalImageRepository", "Invalid bitmap.")
                return null
            }

            // Директория для изображений продуктов
            val imageDir = File(context.filesDir, "images/products")
            if (!imageDir.exists()) {
                imageDir.mkdirs()
            }

            // Сохранение файла
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
}
