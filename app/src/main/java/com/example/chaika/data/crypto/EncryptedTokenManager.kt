package com.example.chaika.data.crypto

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import javax.inject.Inject

/**
 * Реализация EncryptedTokenManagerInterface с использованием EncryptedSharedPreferences.
 */
class EncryptedTokenManager @Inject constructor(context: Context) : EncryptedTokenManagerInterface {

    private val sharedPreferences: SharedPreferences

    init {
        // Создаем мастер-ключ с явным указанием алиаса
        val masterKey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        sharedPreferences = try {
            EncryptedSharedPreferences.create(
                context,
                "encrypted_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            e.printStackTrace()
            // Если произошла ошибка расшифровки, очищаем старое хранилище и пробуем создать заново
            context.getSharedPreferences("encrypted_prefs", Context.MODE_PRIVATE).edit().clear()
                .apply()
            EncryptedSharedPreferences.create(
                context,
                "encrypted_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
    }

    override fun getToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    override fun saveToken(token: String) {
        sharedPreferences.edit().putString("access_token", token).apply()
    }

    override fun clearToken() {
        sharedPreferences.edit().remove("access_token").apply()
    }
}
