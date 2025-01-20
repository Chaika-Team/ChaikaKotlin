package com.example.chaika.data.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class KeyStoreCryptoManager : KeyStoreCryptoManagerInterface {
    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
    private val algorithm = "AES/CBC/PKCS7Padding"

    override fun encrypt(plainData: ByteArray, employeeID: String): ByteArray {
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.ENCRYPT_MODE, getKey(employeeID))
        return cipher.iv + cipher.doFinal(plainData)
    }

    override fun decrypt(encryptedData: ByteArray, employeeID: String): ByteArray {
        val iv = encryptedData.copyOfRange(0, 16)
        val cipherText = encryptedData.copyOfRange(16, encryptedData.size)
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.DECRYPT_MODE, getKey(employeeID), IvParameterSpec(iv))
        return cipher.doFinal(cipherText)
    }

    override fun deleteKey(employeeID: String) {
        keyStore.deleteEntry(getAlias(employeeID))
    }

    private fun getKey(employeeID: String): SecretKey {
        val alias = getAlias(employeeID)
        val entry = keyStore.getEntry(alias, null) as? KeyStore.SecretKeyEntry
        return entry?.secretKey ?: generateKey(alias)
    }

    private fun generateKey(alias: String): SecretKey {
        val keyGenerator =
            KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        keyGenerator.init(
            KeyGenParameterSpec.Builder(
                alias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .build()
        )
        return keyGenerator.generateKey()
    }

    private fun getAlias(employeeID: String) = "key_employee_$employeeID"
}
