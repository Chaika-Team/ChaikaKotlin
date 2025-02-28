@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.example.chaika.domain.usecases

import com.example.chaika.data.local.LocalTripReportRepository
import com.example.chaika.data.room.repo.RoomCartItemRepositoryInterface
import com.example.chaika.data.room.repo.RoomCartOperationRepositoryInterface
import com.example.chaika.data.room.repo.RoomConductorRepositoryInterface
import com.example.chaika.domain.models.CartItemReport
import com.example.chaika.domain.models.CartOperationReport
import com.example.chaika.domain.models.TripReport
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class ReportUseCasesTest {

    @Mock
    lateinit var cartOperationRepository: RoomCartOperationRepositoryInterface

    @Mock
    lateinit var cartItemRepository: RoomCartItemRepositoryInterface

    @Mock
    lateinit var conductorRepository: RoomConductorRepositoryInterface

    @Mock
    lateinit var tripReportRepository: LocalTripReportRepository

    // Dummy данные:
    // Dummy элемент корзины.
    private val dummyCartItemReport = CartItemReport(
        productID = 1,
        quantity = 2,
        price = 10.0
    )

    // Dummy операция с корзиной: поле employeeID передаётся как строка (например, "123").
    private val dummyCartOperationReport = CartOperationReport(
        employeeID = "123",
        operationType = 0,
        operationTime = "2024-10-15T00:12:00+03:00",
        items = listOf(dummyCartItemReport)
    )

    // Пара: идентификатор операции и объект операции.
    private val dummyOperationReportsWithIds = listOf(Pair(1, dummyCartOperationReport))

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: OwletsFox
     *
     * Описание:
     *   - Тест для GenerateTripReportUseCase.
     *   - Сценарий успеха: если все вызовы репозиториев возвращают корректные данные,
     *     use case формирует TripReport, вызывает сохранение отчёта и возвращает true.
     */
    @Test
    fun `GenerateTripReportUseCase returns true on success`() = runTest {
        // Мокаем получение операций.
        whenever(cartOperationRepository.getCartOperationReportsWithIds())
            .thenReturn(flowOf(dummyOperationReportsWithIds))
        // Мокаем получение реального employeeID:
        // dummyCartOperationReport.employeeID == "123" -> toInt() == 123, возвращаем "emp123"
        whenever(conductorRepository.getEmployeeIDByConductorId(123)).thenReturn("emp123")
        // Мокаем получение элементов корзины для операции id = 1.
        whenever(cartItemRepository.getCartItemReportsByOperationId(1))
            .thenReturn(flowOf(dummyCartOperationReport.items))
        // Мокаем сохранение отчёта.
        whenever(tripReportRepository.saveTripReport(any<TripReport>(), any()))
            .thenReturn(true)

        val useCase = GenerateTripReportUseCase(
            cartOperationRepository,
            cartItemRepository,
            conductorRepository,
            tripReportRepository
        )
        val result = useCase.invoke()
        assertTrue(result)
        // Проверяем, что метод сохранения отчёта был вызван.
        verify(tripReportRepository).saveTripReport(any<TripReport>(), any())
    }

    /**
     * Техника тест-дизайна: Прогнозирование ошибок / Классы эквивалентности
     *
     * Автор: OwletsFox
     *
     * Описание:
     *   - Тест для GenerateTripReportUseCase.
     *   - Сценарий неуспеха: если один из вызовов (например, получение операций) бросает исключение,
     *     use case перехватывает его и возвращает false.
     */
    @Test
    fun `GenerateTripReportUseCase returns false when exception occurs`() = runTest {
        // Возвращаем Flow, который при сборе выбрасывает исключение.
        whenever(cartOperationRepository.getCartOperationReportsWithIds())
            .thenReturn(flow { throw Exception("Test exception") })
        // Остальные зависимости замокированы, их поведение не имеет значения.
        val useCase = GenerateTripReportUseCase(
            cartOperationRepository,
            cartItemRepository,
            conductorRepository,
            tripReportRepository
        )
        val result = useCase.invoke()
        assertFalse(result)
    }
}
