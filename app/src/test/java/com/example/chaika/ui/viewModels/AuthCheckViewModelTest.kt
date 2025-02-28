@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.example.chaika.ui.viewModels

import com.example.chaika.testUtils.InstantTaskExecutorExtension
import com.example.chaika.domain.usecases.GetAccessTokenUseCase
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
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class AuthCheckViewModelTest {

    @Suppress("unused")
    @JvmField
    @RegisterExtension
    val instantTaskExecutorExtension = InstantTaskExecutorExtension()

    @Mock
    lateinit var getAccessTokenUseCase: GetAccessTokenUseCase

    private lateinit var viewModel: AuthCheckViewModel

    // Используем тестовый диспетчер для главного потока
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
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
     *   - Тест для ViewModel AuthCheckViewModel.
     *   - Классы эквивалентности: корректное значение access token.
     */
    @Test
    fun `viewModel init sets token from use case (valid token)`() = runTest {
        whenever(getAccessTokenUseCase.invoke()).thenReturn("validToken")
        viewModel = AuthCheckViewModel(getAccessTokenUseCase)
        val token = viewModel.token.getOrAwaitValue()
        assertEquals("validToken", token)
    }

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: OwletsFox
     *
     * Описание:
     *   - Тест для ViewModel AuthCheckViewModel.
     *   - Классы эквивалентности: отсутствие токена (null).
     */
    @Test
    fun `viewModel init sets token from use case (null token)`() = runTest {
        whenever(getAccessTokenUseCase.invoke()).thenReturn(null)
        viewModel = AuthCheckViewModel(getAccessTokenUseCase)
        val token = viewModel.token.getOrAwaitValue()
        assertEquals(null, token)
    }
}
