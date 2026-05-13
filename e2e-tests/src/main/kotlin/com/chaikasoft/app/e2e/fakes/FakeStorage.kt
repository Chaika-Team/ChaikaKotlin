package com.chaikasoft.app.e2e.fakes

import com.chaikasoft.app.data.crypto.EncryptedTokenManagerInterface
import com.chaikasoft.app.data.local.LocalImageRepositoryInterface
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryTokenManager @Inject constructor() : EncryptedTokenManagerInterface {
    @Volatile
    private var token: String? = null

    override fun saveToken(token: String) {
        this.token = token
    }

    override fun getToken(): String? = token

    override fun clearToken() {
        token = null
    }
}

@Singleton
class NoOpLocalImageRepository @Inject constructor() : LocalImageRepositoryInterface {
    override suspend fun saveImageFromUrl(imageUrl: String, fileName: String, subDir: String): String? = null

    override suspend fun deleteImagesInSubDir(subDir: String): Boolean = true
}
