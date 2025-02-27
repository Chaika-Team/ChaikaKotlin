package com.example.chaika.data.crypto

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mockStatic
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Тесты для EncryptedTokenManager.
 *
 * Эти тесты проверяют корректность работы хранилища секретов, реализованного с использованием
 * EncryptedSharedPreferences. Для получения контекста используется Robolectric (через ApplicationProvider),
 * а fallback-логика тестируется с помощью мокирования статического метода EncryptedSharedPreferences.create.
 *
 * Используемые техники тест-дизайна:
 * 1. Классы эквивалентности - проверка типичного сценария сохранения, получения и очистки токена.
 * 2. Граничные значения - проверка сохранения пустой строки как токена.
 * 3. Прогнозирование ошибок / Причинно-следственный анализ - имитация ошибки создания хранилища и проверка fallback-логики.
 */
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class EncryptedTokenManagerTest {
    // Получаем настоящий контекст через Robolectric
    private val context: Context = ApplicationProvider.getApplicationContext()

    /**
     * Техника тест-дизайна: #4 Прогнозирование ошибок / Причинно-следственный анализ
     *
     * Автор: John Doe
     *
     * Описание:
     *   - Тест для EncryptedTokenManager: негативный сценарий, когда первая попытка создания
     *     EncryptedSharedPreferences выбрасывает исключение.
     *   - Прогнозирование ошибок: имитируется ошибка создания хранилища, затем fallback-логика
     *     должна сработать, очистив старое хранилище и создав новое, возвращающее валидный SharedPreferences.
     *
     * Алгоритм теста:
     * 1. Создаем fakePrefs через обычный вызов context.getSharedPreferences(), который будет использоваться как fallback.
     * 2. Мокируем статический метод EncryptedSharedPreferences.create с использованием mockStatic.
     *    - Первая попытка создания бросает исключение (симуляция ошибки).
     *    - Вторая попытка возвращает fakePrefs.
     * 3. Создаем экземпляр EncryptedTokenManager. При инициализации должна сработать fallback-логика.
     * 4. Вызываем saveToken() и проверяем, что токен сохранен корректно.
     */
    @Test
    fun testFallbackOnException() {
        // Подготавливаем fallback SharedPreferences через настоящий контекст.
        val fakePrefs: SharedPreferences =
            context.getSharedPreferences("fake_prefs", Context.MODE_PRIVATE)

        // Мокируем статический метод EncryptedSharedPreferences.create.
        // Используем use{} для автоматического закрытия мока.
        mockStatic(EncryptedSharedPreferences::class.java).use { mockedStatic ->
            // Настраиваем поведение: первая попытка создания выбрасывает исключение, вторая возвращает fakePrefs.
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
                .thenReturn(fakePrefs)

            // Act: создаем экземпляр менеджера токенов; при первой попытке возникает ошибка,
            // и fallback-логика должна сработать, используя fakePrefs.
            val tokenManager = EncryptedTokenManager(context)
            tokenManager.saveToken("fallback_token")

            // Assert: проверяем, что после fallback токен сохранен корректно.
            assertEquals("fallback_token", tokenManager.getToken())
        }
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: John Doe
     *
     * Описание:
     *   - Тест для EncryptedTokenManager: позитивный сценарий, когда EncryptedSharedPreferences создается без ошибок.
     *   - Классы эквивалентности: сохранение, извлечение и очистка токена с типичными значениями.
     *
     * Алгоритм теста:
     * 1. Создаем экземпляр EncryptedTokenManager с настоящим контекстом.
     * 2. Сохраняем токен с ненулевым значением.
     * 3. Проверяем, что метод getToken возвращает сохраненный токен.
     * 4. Вызываем clearToken и убеждаемся, что getToken возвращает null.
     */
    @Test
    fun testSaveGetClearToken() {
        // Act: создаем менеджер токенов и сохраняем токен.
        val tokenManager = EncryptedTokenManager(context)
        tokenManager.saveToken("my_secret_token")

        // Assert: проверяем, что сохраненный токен корректно извлекается.
        assertEquals("my_secret_token", tokenManager.getToken())

        // Act: очищаем токен.
        tokenManager.clearToken()

        // Assert: после очистки getToken должен вернуть null.
        assertNull(tokenManager.getToken())
    }

    /**
     * Техника тест-дизайна: #2 Граничные значения
     *
     * Автор: John Doe
     *
     * Описание:
     *   - Тест для EncryptedTokenManager: сценарий сохранения пустой строки как токена.
     *   - Граничные значения: проверка, что даже пустой токен сохраняется и извлекается корректно.
     *
     * Алгоритм теста:
     * 1. Создаем экземпляр EncryptedTokenManager.
     * 2. Сохраняем пустую строку как токен.
     * 3. Проверяем, что метод getToken возвращает пустую строку.
     */
    @Test
    fun testSaveAndGetEmptyToken() {
        // Act: создаем менеджер токенов и сохраняем пустой токен.
        val tokenManager = EncryptedTokenManager(context)
        tokenManager.saveToken("")

        // Assert: проверяем, что сохраненный пустой токен возвращается как пустая строка.
        assertEquals("", tokenManager.getToken())
    }
}
