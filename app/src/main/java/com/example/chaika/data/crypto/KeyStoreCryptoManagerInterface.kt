package com.example.chaika.data.crypto

/**
 * Интерфейс для операций шифрования и расшифровки паролей.
 */
interface KeyStoreCryptoManagerInterface {

    /**
     * Шифрует строку.
     *
     * @param plainData Обычные данные.
     * @param employeeID Табельный номер, идентификатор ключа шифрования.
     * @return Зашифрованные данные.
     */
    fun encrypt(plainData: ByteArray, employeeID: String): ByteArray

    /**
     * Расшифровывает строку.
     *
     * @param encryptedData Зашифрованные данные.
     * @param employeeID Табельный номер, идентификатор ключа шифрования.
     * @return Расшифрованные данные.
     */
    fun decrypt(encryptedData: ByteArray, employeeID: String): ByteArray

    /**
     * Удаляет ключ, связанный с указанным табельным номером.
     *
     * @param employeeID Табельный номер.
     */
    fun deleteKey(employeeID: String)
}
