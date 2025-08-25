//@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
//
//package com.example.chaika.domain.usecases
//
//import android.content.Intent
//import com.example.chaika.auth.OAuthManager
//import com.example.chaika.data.crypto.EncryptedTokenManagerInterface
//import com.example.chaika.data.local.ImageSubDir
//import com.example.chaika.data.local.LocalImageRepositoryInterface
//import com.example.chaika.domain.models.ConductorDomain
//import kotlinx.coroutines.test.runTest
//import org.junit.jupiter.api.Assertions.assertEquals
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.assertThrows
//import org.junit.jupiter.api.extension.ExtendWith
//import org.mockito.Mock
//import org.mockito.junit.jupiter.MockitoExtension
//import org.mockito.kotlin.any
//import org.mockito.kotlin.doAnswer
//import org.mockito.kotlin.verify
//import org.mockito.kotlin.whenever
//
//@ExtendWith(MockitoExtension::class)
//class AuthUseCasesTest {
//    @Mock
//    lateinit var oAuthManager: OAuthManager
//
//    @Mock
//    lateinit var tokenManager: EncryptedTokenManagerInterface
//
//    @Mock
//    lateinit var deleteAllConductorsUseCase: DeleteAllConductorsUseCase
//
//    @Mock
//    lateinit var imageRepository: LocalImageRepositoryInterface
//
//    private lateinit var startAuthorizationUseCase: StartAuthorizationUseCase
//    private lateinit var handleAuthorizationResponseUseCase: HandleAuthorizationResponseUseCase
//    private lateinit var getAccessTokenUseCase: GetAccessTokenUseCase
//    private lateinit var logoutUseCase: LogoutUseCase
//
//    @BeforeEach
//    fun setUp() {
//        startAuthorizationUseCase = StartAuthorizationUseCase(oAuthManager)
//        handleAuthorizationResponseUseCase =
//            HandleAuthorizationResponseUseCase(oAuthManager, tokenManager)
//        getAccessTokenUseCase = GetAccessTokenUseCase(tokenManager)
//        logoutUseCase = LogoutUseCase(tokenManager, deleteAllConductorsUseCase, imageRepository)
//    }
//
//    /**
//     * Техника тест-дизайна: #1 Классы эквивалентности
//     *
//     * Автор: OwletsFox
//     *
//     * Описание:
//     *  - Тест для StartAuthorizationUseCase.
//     *  - Проверяется, что use case возвращает Intent, созданный методом createAuthIntent() из OAuthManager.
//     */
//    @Test
//    fun `StartAuthorizationUseCase returns correct Intent`() {
//        val expectedIntent = Intent("action_test")
//        whenever(oAuthManager.createAuthIntent()).thenReturn(expectedIntent)
//
//        assertEquals(expectedIntent, startAuthorizationUseCase.invoke())
//    }
//
//    /**
//     * Техника тест-дизайна: #1 Классы эквивалентности
//     *
//     * Автор: OwletsFox
//     *
//     * Описание:
//     *  - Тест для HandleAuthorizationResponseUseCase.
//     *  - Проверяется, что при получении непустого токена use case вызывает tokenManager.saveToken()
//     *    и возвращает полученный токен.
//     */
//    @Test
//    fun `HandleAuthorizationResponseUseCase returns token when valid`() =
//        runTest {
//            val testIntent = Intent("action_test")
//            val validToken = "valid_token"
//            doAnswer { invocation ->
//                invocation.getArgument<(String) -> Unit>(1)(validToken)
//                null
//            }.whenever(oAuthManager).handleAuthorizationResponse(any(), any())
//
//            val resultToken = handleAuthorizationResponseUseCase.invoke(testIntent)
//            verify(tokenManager).saveToken(validToken)
//            assertEquals(validToken, resultToken)
//        }
//
//    /**
//     * Техника тест-дизайна: Прогнозирование ошибок / Классы эквивалентности
//     *
//     * Автор: OwletsFox
//     *
//     * Описание:
//     *  - Тест для HandleAuthorizationResponseUseCase.
//     *  - Проверяется, что если возвращённый токен пустой, use case выбрасывает исключение с сообщением "Получен пустой токен".
//     */
//    @Test
//    fun `HandleAuthorizationResponseUseCase throws exception when token is empty`() =
//        runTest {
//            val intent = Intent()
//            doAnswer { invocation ->
//                invocation.getArgument<(String) -> Unit>(1)("")
//                null
//            }.whenever(oAuthManager).handleAuthorizationResponse(any(), any())
//
//            val ex = assertThrows<Exception> { handleAuthorizationResponseUseCase.invoke(intent) }
//            assertEquals("Получен пустой токен", ex.message)
//        }
//
//    /**
//     * Техника тест-дизайна: #1 Классы эквивалентности
//     *
//     * Автор: OwletsFox
//     *
//     * Описание:
//     *  - Тест для GetAccessTokenUseCase.
//     *  - Проверяется, что use case возвращает токен, полученный от tokenManager.
//     */
//    @Test
//    fun `GetAccessTokenUseCase returns token from tokenManager`() =
//        runTest {
//            whenever(tokenManager.getToken()).thenReturn("stored")
//            assertEquals("stored", getAccessTokenUseCase.invoke())
//        }
//
//    /**
//     * Техника тест-дизайна: #1 Классы эквивалентности
//     *
//     * Автор: OwletsFox
//     *
//     * Описание:
//     *  - Тест для LogoutUseCase.
//     *  - Проверяется, что use case вызывает методы очистки токена (clearToken()),
//     *    удаления всех проводников (deleteAllConductorsUseCase.invoke()) и удаления изображений (deleteImagesInSubDir()).
//     */
//    @Test
//    fun `LogoutUseCase calls clearToken, deleteAllConductors and deleteImages`() =
//        runTest {
//            logoutUseCase.invoke()
//            verify(tokenManager).clearToken()
//            verify(deleteAllConductorsUseCase).invoke()
//            verify(imageRepository).deleteImagesInSubDir(ImageSubDir.CONDUCTORS.folder)
//        }
//}
//
//@ExtendWith(MockitoExtension::class)
//class CompleteAuthorizationFlowUseCaseTest {
//    @Mock
//    lateinit var handleUseCase: HandleAuthorizationResponseUseCase
//
//    @Mock
//    lateinit var authorizeUseCase: AuthorizeAndSaveConductorUseCase
//
//    private lateinit var completeFlow: CompleteAuthorizationFlowUseCase
//
//    @BeforeEach
//    fun setUp() {
//        completeFlow = CompleteAuthorizationFlowUseCase(handleUseCase, authorizeUseCase)
//    }
//
//    /** Тест для успешного флоу CompleteAuthorizationFlowUseCase
//     *
//     * Автор: Fascinat0r
//     */
//    @Test
//    fun `CompleteAuthorizationFlowUseCase returns token and conductor`() =
//        runTest {
//            val intent = Intent()
//            val token = "token"
//            val conductor = ConductorDomain(1, "A", "B", "C", "ID", "img")
//
//            whenever(handleUseCase.invoke(intent)).thenReturn(token)
//            whenever(authorizeUseCase.invoke(token)).thenReturn(conductor)
//
//            val (resultToken, resultConductor) = completeFlow.invoke(intent)
//            assertEquals(token, resultToken)
//            assertEquals(conductor, resultConductor)
//        }
//
//    /** Тест: ошибка при получении токена
//     *
//     * Автор: Fascinat0r
//     */
//    @Test
//    fun `CompleteAuthorizationFlowUseCase throws when handle fails`() =
//        runTest {
//            whenever(handleUseCase.invoke(any())).thenThrow(RuntimeException("fail"))
//            assertThrows<RuntimeException> { completeFlow.invoke(Intent()) }
//        }
//
//    /** Тест: ошибка при получении conductor
//     *
//     * Автор: Fascinat0r
//     */
//    @Test
//    fun `CompleteAuthorizationFlowUseCase throws when authorize fails`() =
//        runTest {
//            whenever(handleUseCase.invoke(any())).thenReturn("token")
//            whenever(authorizeUseCase.invoke(any())).thenThrow(RuntimeException("fail"))
//            assertThrows<RuntimeException> { completeFlow.invoke(Intent()) }
//        }
//}
