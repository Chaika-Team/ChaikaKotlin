@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.example.chaika.domain.usecases

import com.example.chaika.data.room.repo.RoomReportRepositoryInterface
import com.example.chaika.domain.models.FastReportDomain
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.kotlin.whenever
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class FastReportUseCasesTest {

    @Mock
    lateinit var reportRepository: RoomReportRepositoryInterface

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: OwletsFox
     *
     * Описание:
     *   - Тест для GetFastReportDataUseCase.
     *   - Проверяется, что use case возвращает Flow со списком FastReportDomain,
     *     полученным из репозитория.
     *   - Для этого мокается метод getFastReportData(), который возвращает Flow с dummy‑данными.
     */
    @Test
    fun `GetFastReportDataUseCase returns correct flow data`() = runTest {
        val dummyReport = FastReportDomain(
            productName = "Product1",
            productPrice = 100.0,
            addedQuantity = 10,
            replenishedQuantity = 5,
            soldCashQuantity = 3,
            soldCartQuantity = 2,
            revenue = 300.0
        )
        val expectedList = listOf(dummyReport)
        whenever(reportRepository.getFastReportData()).thenReturn(flowOf(expectedList))

        val useCase = GetFastReportDataUseCase(reportRepository)
        val actualList = useCase.invoke().first()
        assertEquals(expectedList, actualList)
    }
}
