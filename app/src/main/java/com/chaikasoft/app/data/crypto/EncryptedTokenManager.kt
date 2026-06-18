package com.chaikasoft.app.data.crypto

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.io.IOException
import java.security.GeneralSecurityException
import javax.inject.Inject

class EncryptedTokenManager @Inject constructor(context: Context) : EncryptedTokenManagerInterface {

    private val sharedPreferences: SharedPreferences

    init {
        val masterKey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        sharedPreferences = try {
            createEncryptedPreferences(context, masterKey)
        } catch (e: GeneralSecurityException) {
            resetEncryptedPreferences(context, masterKey, e)
        } catch (e: IOException) {
            resetEncryptedPreferences(context, masterKey, e)
        }
    }

    private fun createEncryptedPreferences(
        context: Context,
        masterKey: MasterKey
    ): SharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private fun resetEncryptedPreferences(
        context: Context,
        masterKey: MasterKey,
        cause: Exception
    ): SharedPreferences {
        Log.w(TAG, "Failed to open encrypted preferences. Resetting token storage.", cause)
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().clear().apply()
        return createEncryptedPreferences(context, masterKey)
    }

    override fun getToken(): String? = sharedPreferences.getString(ACCESS_TOKEN_KEY, null)

    override fun saveToken(token: String) {
        sharedPreferences.edit().putString(ACCESS_TOKEN_KEY, token).apply()
    }

    override fun clearToken() {
        sharedPreferences.edit().remove(ACCESS_TOKEN_KEY).apply()
    }

    private companion object {
        const val TAG = "EncryptedTokenManager"
        const val PREFS_NAME = "encrypted_prefs"
        const val ACCESS_TOKEN_KEY = "access_token"
    }
}
