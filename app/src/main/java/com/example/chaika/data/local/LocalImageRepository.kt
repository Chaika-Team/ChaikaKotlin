package com.example.chaika.data.local

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class LocalImageRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun saveImageFromUrl(imageUrl: String, fileName: String): String? {
        return try {
            // Выполняем загрузку изображения в фоновом потоке
            val bitmap = withContext(Dispatchers.IO) {
                Glide.with(context)
                    .asBitmap()
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .submit()
                    .get()
            }

            // Проверка, что Bitmap корректен и не пустой
            if (bitmap == null || bitmap.width <= 0 || bitmap.height <= 0) {
                Log.e("ImageSaver", "Bitmap is invalid or empty")
                return null
            } else {
                Log.d(
                    "ImageSaver",
                    "Bitmap loaded successfully: width=${bitmap.width}, height=${bitmap.height}"
                )
            }

            // Сохраняем Bitmap во внутреннюю память
            val file = File(context.filesDir, fileName)
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            Log.d("ImageSaver", "Image saved successfully: ${file.absolutePath}")
            file.absolutePath // Возвращаем путь к сохранённому изображению
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("ImageSaver", "Failed to save image: ${e.message}")
            null // Возвращаем null, если произошла ошибка
        }
    }
}
