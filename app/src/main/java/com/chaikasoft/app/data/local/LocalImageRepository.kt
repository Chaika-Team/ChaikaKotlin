package com.chaikasoft.app.data.local

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.chaikasoft.app.di.IoDispatcher
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class LocalImageRepository
@Inject
constructor(
    private val context: Context,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val bitmapLoader: suspend (Context, String) -> Bitmap? = { ctx, url ->
        Glide
            .with(ctx)
            .asBitmap()
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .submit()
            .get()
    },
    private val deleteRecursively: (File) -> Boolean = { it.deleteRecursively() }
) : LocalImageRepositoryInterface {
    override suspend fun saveImageFromUrl(
        imageUrl: String,
        fileName: String,
        subDir: String
    ): String? = withContext(ioDispatcher) {
        try {
            val bitmap = bitmapLoader(context, imageUrl)

            if (bitmap == null || bitmap.width <= 0 || bitmap.height <= 0) {
                Log.e("LocalImageRepository", "Invalid bitmap.")
                return@withContext null
            }

            val imageDir = File(context.filesDir, "images/$subDir")
            if (!imageDir.exists()) {
                imageDir.mkdirs()
            }

            val file = File(imageDir, fileName)
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }

            Log.d("LocalImageRepository", "Image saved: ${file.absolutePath}")
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("LocalImageRepository", "Failed to save image: ${e.message}")
            null
        }
    }

    override suspend fun deleteImagesInSubDir(subDir: String): Boolean = withContext(ioDispatcher) {
        try {
            val targetDir = File(context.filesDir, "images/$subDir")
            if (targetDir.exists()) {
                val deleted = deleteRecursively(targetDir)
                Log.d(
                    "LocalImageRepository",
                    "Deleted images from: ${targetDir.absolutePath}, result: $deleted"
                )
            } else {
                Log.d(
                    "LocalImageRepository",
                    "Directory does not exist: ${targetDir.absolutePath}"
                )
            }
            true
        } catch (e: Exception) {
            Log.e(
                "LocalImageRepository",
                "Failed to delete images in subDir '$subDir': ${e.message}"
            )
            false
        }
    }
}
