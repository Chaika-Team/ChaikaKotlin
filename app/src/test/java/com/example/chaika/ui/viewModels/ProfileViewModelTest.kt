@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.example.chaika.ui.viewModels

import com.example.chaika.testUtils.InstantTaskExecutorExtension
import com.example.chaika.domain.models.ConductorDomain
import com.example.chaika.domain.usecases.GetAllConductorsUseCase
import com.example.chaika.domain.usecases.LogoutUseCase
import com.example.chaika.testUtils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.kotlin.whenever
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class ProfileViewModelTest {

    @Suppress("unused")
    @JvmField
    @RegisterExtension
    val instantTaskExecutorExtension = InstantTaskExecutorExtension()

    @Mock
    lateinit var getAllConductorsUseCase: GetAllConductorsUseCase

    @Mock
    lateinit var logoutUseCase: LogoutUseCase

    private lateinit var viewModel: ProfileViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    private val dummyConductor = ConductorDomain(
        id = 1,
        name = "John",
        familyName = "Doe",
        givenName = "Dodoe",
        employeeID = "123",
        image = "img"
    )

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        // Настраиваем use case так, чтобы он возвращал flow с dummyConductor
        whenever(getAllConductorsUseCase.invoke()).thenReturn(flowOf(listOf(dummyConductor)))
        viewModel = ProfileViewModel(getAllConductorsUseCase, logoutUseCase)
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
     *  - Тест для ProfileViewModel.
     *  - При наличии данных проводника, LiveData conductor должна содержать первый элемент списка.
     */
    @Test
    fun `init sets conductor LiveData with first conductor`() = runTest {
        val conductor = viewModel.conductor.getOrAwaitValue()
        assertEquals(dummyConductor, conductor)
    }

    /**
     * Техника тест-дизайна: Прогнозирование ошибок / Классы эквивалентности
     *
     * Автор: OwletsFox
     *
     * Описание:
     *  - Тест для ProfileViewModel.
     *  - Метод logout должен обновлять LiveData logoutSuccess в true.
     */
    @Test
    fun `logout sets logoutSuccess to true`() = runTest {
        // Если logoutUseCase не выбрасывает исключения, то после вызова logout logoutSuccess должна стать true
        viewModel.logout()
        val logoutSuccess = viewModel.logoutSuccess.getOrAwaitValue()
        assertTrue(logoutSuccess)
    }
}
