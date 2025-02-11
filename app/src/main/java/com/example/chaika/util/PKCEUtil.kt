package com.example.chaika.util

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom

object PKCEUtil {
    fun generateCodeVerifier(length: Int = 64): String {
        val secureRandom = SecureRandom()
        val code = ByteArray(length)
        secureRandom.nextBytes(code)
        return Base64.encodeToString(code, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
    }

    fun generateCodeChallenge(codeVerifier: String): String {
        val bytes = codeVerifier.toByteArray(Charsets.US_ASCII)
        val digest = MessageDigest.getInstance("SHA-256").digest(bytes)
        return Base64.encodeToString(digest, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
    }
}
