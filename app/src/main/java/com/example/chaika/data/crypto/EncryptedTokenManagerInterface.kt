package com.example.chaika.data.crypto

/**
 * Интерфейс для работы с токенами доступа.
 */
interface EncryptedTokenManagerInterface {
    fun saveToken(token: String)
    fun getToken(): String?
    fun clearToken()
}
