package com.example.chaika.data.crypto

class FakeEncryptedTokenManager : EncryptedTokenManagerInterface {
    override fun saveToken(token: String) {
        // Для теста не требуется сохранять токен
    }

    override fun getToken(): String? {
        // Возвращаем заранее заданный токен
        return "fake_token"
    }

    override fun clearToken() {
        // Ничего не делаем
    }
}
