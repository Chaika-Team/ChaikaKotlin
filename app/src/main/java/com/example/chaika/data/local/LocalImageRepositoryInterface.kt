package com.example.chaika.data.local

interface LocalImageRepositoryInterface {
    suspend fun saveImageFromUrl(imageUrl: String, fileName: String, subDir: String): String?
    suspend fun deleteImagesInSubDir(subDir: String): Boolean
}
