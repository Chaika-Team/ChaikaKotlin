@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.example.chaika.domain.usecases

import android.content.Intent
import com.example.chaika.auth.OAuthManager
import com.example.chaika.data.crypto.EncryptedTokenManagerInterface
import com.example.chaika.data.local.ImageSubDir
import com.example.chaika.data.local.LocalImageRepositoryInterface
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.any
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.verify
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class AuthUseCasesTest {

    @Mock
    lateinit var oAuthManager: OAuthManager

    @Mock
    lateinit var tokenManager: EncryptedTokenManagerInterface

    @Mock
    lateinit var deleteAllConductorsUseCase: DeleteAllConductorsUseCase

    @Mock
    lateinit var imageRepository: LocalImageRepositoryInterface

    private lateinit var startAuthorizationUseCase: StartAuthorizationUseCase
    private lateinit var handleAuthorizationResponseUseCase: HandleAuthorizationResponseUseCase
    private lateinit var getAccessTokenUseCase: GetAccessTokenUseCase
    private lateinit var logoutUseCase: LogoutUseCase

    @BeforeEach
    fun setUp() {
        startAuthorizationUseCase = StartAuthorizationUseCase(oAuthManager)
        handleAuthorizationResponseUseCase =
            HandleAuthorizationResponseUseCase(oAuthManager, tokenManager)
        getAccessTokenUseCase = GetAccessTokenUseCase(tokenManager)
        logoutUseCase = LogoutUseCase(tokenManager, deleteAllConductorsUseCase, imageRepository)
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: OwletsFox
     *
     * Описание:
     *  - Тест для StartAuthorizationUseCase.
     *  - Проверяется, что use case возвращает Intent, созданный методом createAuthIntent() из OAuthManager.
     */
    @Test
    fun `StartAuthorizationUseCase returns correct Intent`() {
        val expectedIntent = Intent("action_test")
        `when`(oAuthManager.createAuthIntent()).thenReturn(expectedIntent)

        val actualIntent = startAuthorizationUseCase.invoke()
        assertEquals(expectedIntent, actualIntent)
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: OwletsFox
     *
     * Описание:
     *  - Тест для HandleAuthorizationResponseUseCase.
     *  - Проверяется, что при получении непустого токена use case вызывает tokenManager.saveToken()
     *    и возвращает полученный токен.
     */
    @Test
    fun `HandleAuthorizationResponseUseCase returns token when valid`() = runTest {
        val testIntent = Intent("action_test")
        val validToken = "valid_token"
        doAnswer { invocation ->
            val callback = invocation.getArgument<(String) -> Unit>(1)
            callback(validToken)
            null
        }.`when`(oAuthManager).handleAuthorizationResponse(any(), any<(String) -> Unit>())

        val resultToken = handleAuthorizationResponseUseCase.invoke(testIntent)
        verify(tokenManager).saveToken(validToken)
        assertEquals(validToken, resultToken)
    }

    /**
     * Техника тест-дизайна: Прогнозирование ошибок / Классы эквивалентности
     *
     * Автор: OwletsFox
     *
     * Описание:
     *  - Тест для HandleAuthorizationResponseUseCase.
     *  - Проверяется, что если возвращённый токен пустой, use case выбрасывает исключение с сообщением "Получен пустой токен".
     */
    @Test
    fun `HandleAuthorizationResponseUseCase throws exception when token is empty`() = runTest {
        val testIntent = Intent("action_test")
        val emptyToken = ""
        doAnswer { invocation ->
            val callback = invocation.getArgument<(String) -> Unit>(1)
            callback(emptyToken)
            null
        }.`when`(oAuthManager).handleAuthorizationResponse(any(), any<(String) -> Unit>())

        val exception = assertThrows<Exception> {
            handleAuthorizationResponseUseCase.invoke(testIntent)
        }
        assertEquals("Получен пустой токен", exception.message)
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: OwletsFox
     *
     * Описание:
     *  - Тест для GetAccessTokenUseCase.
     *  - Проверяется, что use case возвращает токен, полученный от tokenManager.
     */
    @Test
    fun `GetAccessTokenUseCase returns token from tokenManager`() = runTest {
        val storedToken = "stored_token"
        `when`(tokenManager.getToken()).thenReturn(storedToken)

        val result = getAccessTokenUseCase.invoke()
        assertEquals(storedToken, result)
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: OwletsFox
     *
     * Описание:
     *  - Тест для LogoutUseCase.
     *  - Проверяется, что use case вызывает методы очистки токена (clearToken()),
     *    удаления всех проводников (deleteAllConductorsUseCase.invoke()) и удаления изображений (deleteImagesInSubDir()).
     */
    @Test
    fun `LogoutUseCase calls clearToken, deleteAllConductorsUseCase and deleteImagesInSubDir`() =
        runTest {
            logoutUseCase.invoke()

            verify(tokenManager).clearToken()
            verify(deleteAllConductorsUseCase).invoke()
            verify(imageRepository).deleteImagesInSubDir(ImageSubDir.CONDUCTORS.folder)
        }
}
