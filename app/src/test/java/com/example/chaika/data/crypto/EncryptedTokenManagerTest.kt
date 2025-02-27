package com.example.chaika.data.crypto

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.mockito.MockedStatic
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor

/**
 * Тесты для класса EncryptedTokenManager.
 */
class EncryptedTokenManagerTest {

    private lateinit var context: Context
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var encryptedTokenManager: EncryptedTokenManager

    private lateinit var encryptedSharedPrefsStatic: MockedStatic<EncryptedSharedPreferences>
    private lateinit var masterKeyStatic: MockedStatic<MasterKey.Builder>

    @BeforeEach
    fun setup() {
        // Создаем мок контекста
        context = mock(Context::class.java)
        // Мокаем SharedPreferences и Editor
        sharedPrefs = mock(SharedPreferences::class.java)
        editor = mock(SharedPreferences.Editor::class.java)

        // Настраиваем поведение editor
        `when`(editor.putString(any(), any())).thenReturn(editor)
        `when`(editor.remove(any())).thenReturn(editor)
        doNothing().`when`(editor).apply()

        // Настраиваем поведение контекста для getSharedPreferences (используется в catch)
        `when`(context.getSharedPreferences(any(), anyInt())).thenReturn(sharedPrefs)
        `when`(sharedPrefs.edit()).thenReturn(editor)

        // Мокаем MasterKey.Builder и MasterKey
        val masterKey = mock(MasterKey::class.java)
        val masterKeyBuilder = mock(MasterKey.Builder::class.java)
        // Для простоты, настройте masterKeyBuilder.build() чтобы возвращал masterKey
        `when`(masterKeyBuilder.setKeyScheme(any())).thenReturn(masterKeyBuilder)
        `when`(masterKeyBuilder.build()).thenReturn(masterKey)

        // Мокаем статический метод EncryptedSharedPreferences.create()
        encryptedSharedPrefsStatic = mockStatic(EncryptedSharedPreferences::class.java)
        // Настраиваем успешное создание шифрованного SharedPreferences
        encryptedSharedPrefsStatic.when {
            EncryptedSharedPreferences.create(
                any<Context>(),
                any<String>(),
                any<MasterKey>(),
                any(),
                any()
            )
        }.thenReturn(sharedPrefs)

        // Теперь создаем экземпляр EncryptedTokenManager с замоканным контекстом
        encryptedTokenManager = EncryptedTokenManager(context)
    }

    @AfterEach
    fun tearDown() {
        encryptedSharedPrefsStatic.close()
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: John Doe
     *
     * Описание:
     *   - Тест для метода getToken.
     *   - Классы эквивалентности: когда токен не установлен, getToken возвращает null.
     */
    @Test
    fun testGetToken_whenNotSet_returnsNull() {
        // Arrange: убеждаемся, что getString возвращает null
        `when`(sharedPrefs.getString("access_token", null)).thenReturn(null)

        // Act
        val token = encryptedTokenManager.getToken()

        // Assert
        assertNull(token)
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: John Doe
     *
     * Описание:
     *   - Тест для метода saveToken.
     *   - Классы эквивалентности: корректное сохранение непустого токена.
     */
    @Test
    fun testSaveToken_savesTokenCorrectly() {
        // Arrange
        val testToken = "secret_token"
        // Будем использовать аргумент-каптор для проверки вызова putString
        val keyCaptor = argumentCaptor<String>()
        val valueCaptor = argumentCaptor<String>()
        // Act
        encryptedTokenManager.saveToken(testToken)
        // Assert
        verify(sharedPrefs.edit(), times(1)).putString(keyCaptor.capture(), valueCaptor.capture())
        verify(sharedPrefs.edit(), times(1)).apply()
        assertEquals("access_token", keyCaptor.firstValue)
        assertEquals(testToken, valueCaptor.firstValue)
    }

    /**
     * Техника тест-дизайна: #4 Прогнозирование ошибок
     *
     * Автор: John Doe
     *
     * Описание:
     *   - Тест для метода clearToken.
     *   - Прогнозирование ошибок: проверяется, что метод remove вызывается корректно.
     */
    @Test
    fun testClearToken_removesToken() {
        // Arrange
        // Act
        encryptedTokenManager.clearToken()
        // Assert
        verify(sharedPrefs.edit(), times(1)).remove("access_token")
        verify(sharedPrefs.edit(), times(1)).apply()
    }

    /**
     * Техника тест-дизайна: #3 Причинно-следственный анализ
     *
     * Автор: John Doe
     *
     * Описание:
     *   - Тест для сценария, когда создание EncryptedSharedPreferences выбрасывает исключение.
     *   - Симулируется ошибка первого вызова, затем успешное создание.
     */
    @Test
    fun testInit_whenFirstCallFails_thenClearsAndRecreates() {
        // Закрываем предыдущий мок
        encryptedSharedPrefsStatic.close()

        // Мокаем так, чтобы первый вызов бросал исключение, а второй возвращал sharedPrefs.
        val mockedStatic = mockStatic(EncryptedSharedPreferences::class.java)
        var callCount = 0
        mockedStatic.`when` {
            EncryptedSharedPreferences.create(
                any<Context>(),
                any<String>(),
                any<MasterKey>(),
                any(),
                any()
            )
        }.thenAnswer {
            callCount++
            if (callCount == 1) throw RuntimeException("Decryption error") else sharedPrefs
        }

        // Дополнительно замокать вызов getSharedPreferences() уже настроен в setup()

        // Создаем новый экземпляр менеджера, чтобы инициализация повторилась
        val manager = EncryptedTokenManager(context)

        // Проверяем, что повторный вызов прошел успешно
        assertNotNull(manager.getToken()) // т.к. если токен не сохранен, вернет null, но создание sharedPrefs прошло
        // Закрываем мок
        mockedStatic.close()
    }
}
