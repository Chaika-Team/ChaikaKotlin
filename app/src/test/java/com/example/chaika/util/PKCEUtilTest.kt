@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.example.chaika.util

import android.util.Base64
import com.example.chaika.testUtils.InstantTaskExecutorExtension
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.MockedStatic
import org.mockito.Mockito
import java.security.MessageDigest

/**
 * Тесты для PKCEUtil
 *
 * Техники тест-дизайна:
 *   - #1 Классы эквивалентности
 *   - #2 Граничные значения
 *
 * Автор: OwletsFox
 */
@ExtendWith(InstantTaskExecutorExtension::class)
class PKCEUtilTest {

    companion object {
        private lateinit var base64Mock: MockedStatic<Base64>

        @BeforeAll
        @JvmStatic
        fun setUp() {
            // Мокируем поведение Base64.encodeToString()
            base64Mock = Mockito.mockStatic(Base64::class.java)
            base64Mock.`when`<String> {
                Base64.encodeToString(Mockito.any(), Mockito.anyInt())
            }.thenAnswer { invocation ->
                java.util.Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(invocation.getArgument(0))
            }
        }
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Описание:
     *   - Проверяется корректность генерации code verifier с использованием метода generateCodeVerifier().
     *   - Класс эквивалентности: длина code verifier равна 64 символам (по умолчанию).
     *   - Ожидается, что сгенерированная строка будет состоять из URL-безопасных символов и иметь нужную длину.
     */
    @Test
    fun `generateCodeVerifier returns correct length and format`() {
        val codeVerifier = PKCEUtil.generateCodeVerifier()
        assertEquals(86, codeVerifier.length) // Длина Base64 строки для 64 байтов
        assertTrue(codeVerifier.matches(Regex("^[A-Za-z0-9\\-_.~]*$")))
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Описание:
     *   - Проверяется возможность генерации code verifier разной длины.
     *   - Класс эквивалентности: передаётся длина в байтах, отличная от значения по умолчанию.
     *   - Ожидается, что сгенерированная строка будет корректной длины в формате Base64.
     */
    @Test
    fun `generateCodeVerifier returns correct length for different input`() {
        val length = 32
        val codeVerifier = PKCEUtil.generateCodeVerifier(length)
        assertEquals(43, codeVerifier.length) // Длина Base64 строки для 32 байтов
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Описание:
     *   - Проверяется корректность генерации code challenge на основе code verifier.
     *   - Класс эквивалентности: передаётся корректный code verifier.
     *   - Ожидается, что сгенерированный code challenge будет в формате Base64.
     */
    @Test
    fun `generateCodeChallenge returns correct format`() {
        val codeVerifier = "example_code_verifier"
        val codeChallenge = PKCEUtil.generateCodeChallenge(codeVerifier)
        assertTrue(codeChallenge.matches(Regex("^[A-Za-z0-9\\-_.~]*$")))
    }

    /**
     * Техника тест-дизайна: #2 Граничные значения
     *
     * Описание:
     *   - Проверяется соответствие результата метода generateCodeChallenge() вручную вычисленному значению.
     *   - Класс эквивалентности: корректный code verifier.
     *   - Ожидается, что результат будет совпадать с заранее вычисленным хешем в формате Base64.
     */
    @Test
    fun `generateCodeChallenge returns correct value for known verifier`() {
        val codeVerifier = "test_code_verifier"
        val bytes = codeVerifier.toByteArray(Charsets.US_ASCII)
        val digest = MessageDigest.getInstance("SHA-256").digest(bytes)
        val expectedChallenge =
            java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(digest)
        val actualChallenge = PKCEUtil.generateCodeChallenge(codeVerifier)
        assertEquals(expectedChallenge, actualChallenge)
    }
}
