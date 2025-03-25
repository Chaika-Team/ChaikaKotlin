@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.example.chaika.ui.viewModels

import android.content.Intent
import com.example.chaika.domain.models.ConductorDomain
import com.example.chaika.domain.usecases.CompleteAuthorizationFlowUseCase
import com.example.chaika.domain.usecases.StartAuthorizationUseCase
import com.example.chaika.testUtils.InstantTaskExecutorExtension
import com.example.chaika.testUtils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class AuthViewModelTest {
    @Suppress("unused")
    @JvmField
    @RegisterExtension
    val instantTaskExecutorExtension = InstantTaskExecutorExtension()

    @Mock
    lateinit var startAuthorizationUseCase: StartAuthorizationUseCase

    @Mock
    lateinit var completeAuthorizationFlowUseCase: CompleteAuthorizationFlowUseCase

    private lateinit var viewModel: AuthViewModel

    // Используем тестовый диспетчер для главного потока
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AuthViewModel(startAuthorizationUseCase, completeAuthorizationFlowUseCase)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: OwletsFox
     *
     * Описание:
     *  - Тест для AuthViewModel.
     *  - Проверка getAuthIntent(): должен вернуть Intent, созданный use case.
     */
    @Test
    fun `getAuthIntent returns correct Intent`() {
        val expectedIntent = Intent("action_test")
        whenever(startAuthorizationUseCase.invoke()).thenReturn(expectedIntent)

        val actualIntent = viewModel.getAuthIntent()

        assertEquals(expectedIntent, actualIntent)
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: OwletsFox
     *
     * Описание:
     *  - Тест для AuthViewModel.
     *  - При корректном deep link, processDeepLink должен обновить accessToken.
     */
    @Test
    fun `processDeepLink sets accessToken when use case succeeds`() =
        runTest {
            val testIntent = Intent("action_deeplink")
            val expectedToken = "token123"
            val dummyConductor =
                ConductorDomain(
                    id = 1,
                    name = "John",
                    familyName = "Doe",
                    givenName = "John",
                    employeeID = "123",
                    image = "img",
                )

            whenever(completeAuthorizationFlowUseCase.invoke(testIntent))
                .thenReturn(expectedToken to dummyConductor)

            viewModel.processDeepLink(testIntent)

            val actualToken = viewModel.accessToken.getOrAwaitValue()
            assertEquals(expectedToken, actualToken)
        }

    /**
     * Техника тест-дизайна: Прогнозирование ошибок / Классы эквивалентности
     *
     * Автор: OwletsFox
     *
     * Описание:
     *  - Тест для AuthViewModel.
     *  - Если CompleteAuthorizationFlowUseCase выбрасывает исключение, то error должен быть обновлён.
     */
    @Test
    fun `processDeepLink sets error when use case throws exception`() =
        runTest {
            val testIntent = Intent("action_deeplink")
            val exceptionMessage = "Invalid authorization"
            whenever(completeAuthorizationFlowUseCase.invoke(testIntent))
                .thenThrow(RuntimeException(exceptionMessage))

            viewModel.processDeepLink(testIntent)

            val actualError = viewModel.error.getOrAwaitValue()
            assertEquals(exceptionMessage, actualError)
        }
}
