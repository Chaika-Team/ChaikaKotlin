package com.example.chaika.data.crypto

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mockStatic
import org.mockito.kotlin.any
import org.mockito.kotlin.eq

/**
 * Тесты для EncryptedTokenManager.
 */
class EncryptedTokenManagerTest {
    // Используем настоящий контекст, предоставляемый Robolectric
    private val context: Context = ApplicationProvider.getApplicationContext()

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: John Doe
     *
     * Описание:
     *   - Тест для EncryptedTokenManager: позитивный сценарий, когда EncryptedSharedPreferences создается без ошибок.
     *   - Классы эквивалентности: сохранение, извлечение и очистка токена с типичными значениями.
     */
    @Test
    fun testSaveGetClearToken() =
        runBlocking {
            // Создаем экземпляр менеджера токенов с настоящим контекстом
            val tokenManager = EncryptedTokenManager(context)

            // Сохраняем тестовый токен
            tokenManager.saveToken("my_secret_token")
            assertEquals(
                "my_secret_token",
                tokenManager.getToken(),
                "Сохраненный токен должен быть извлечен корректно",
            )

            // Очищаем токен
            tokenManager.clearToken()
            assertNull(tokenManager.getToken(), "После очистки токен должен быть равен null")
        }

    /**
     * Техника тест-дизайна: #2 Граничные значения
     *
     * Автор: John Doe
     *
     * Описание:
     *   - Тест для EncryptedTokenManager: сценарий сохранения пустой строки как токена.
     *   - Граничные значения: проверка, что даже пустой токен сохраняется и извлекается корректно.
     */
    @Test
    fun testSaveAndGetEmptyToken() =
        runBlocking {
            val tokenManager = EncryptedTokenManager(context)
            tokenManager.saveToken("")
            assertEquals(
                "",
                tokenManager.getToken(),
                "Сохраненный пустой токен должен возвращаться как пустая строка",
            )
        }

    /**
     * Техника тест-дизайна: #4 Прогнозирование ошибок / Причинно-следственный анализ
     *
     * Автор: John Doe
     *
     * Описание:
     *   - Тест для EncryptedTokenManager: негативный сценарий, когда первая попытка создания EncryptedSharedPreferences выбрасывает исключение.
     *   - Прогнозирование ошибок: имитируется ошибка создания хранилища, затем fallback-логика очищает старое хранилище и создает новое.
     */
    @Test
    fun testFallbackOnException() =
        runBlocking {
            // Мокируем статический метод EncryptedSharedPreferences.create с использованием Mockito‑inline.
            val mockedStatic = mockStatic(EncryptedSharedPreferences::class.java)
            try {
                // Первая попытка создания бросает исключение, вторая – возвращает реальный SharedPreferences.
                mockedStatic
                    .`when`<SharedPreferences> {
                        EncryptedSharedPreferences.create(
                            any<Context>(),
                            eq("encrypted_prefs"),
                            any<MasterKey>(),
                            eq(EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV),
                            eq(EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM),
                        )
                    }.thenThrow(RuntimeException("Simulated error"))
                    .thenCallRealMethod() // Вторая попытка — вызов реального метода

                // Создаем экземпляр менеджера токенов
                val tokenManager = EncryptedTokenManager(context)
                // Сохраняем токен через fallback
                tokenManager.saveToken("fallback_token")
                // Проверяем, что токен сохранен
                assertEquals(
                    "fallback_token",
                    tokenManager.getToken(),
                    "После fallback токен должен быть сохранен корректно",
                )
            } finally {
                mockedStatic.close()
            }
        }
}
