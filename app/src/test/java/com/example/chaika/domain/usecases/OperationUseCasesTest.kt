@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.example.chaika.domain.usecases

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.testing.asSnapshot
import com.example.chaika.data.room.repo.RoomCartOperationRepositoryInterface
import com.example.chaika.domain.models.CartDomain
import com.example.chaika.domain.models.CartItemDomain
import com.example.chaika.domain.models.ConductorDomain
import com.example.chaika.domain.models.OperationSummaryDomain
import com.example.chaika.domain.models.OperationTypeDomain
import com.example.chaika.domain.models.ProductInfoDomain
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class OperationUseCasesTest {

    @Mock
    lateinit var repository: RoomCartOperationRepositoryInterface

    /**
     * Техника тест-дизайна: #1 Классы эквивалентности
     *
     * Автор: OwletsFox
     *
     * Описание:
     *   - GetPagedOperationSummariesUseCase возвращает Flow<PagingData<OperationSummaryDomain>>,
     *     полученный из репозитория.
     */
    @Test
    fun `GetPagedOperationSummariesUseCase returns paging data from repository`() = runTest {
        // Arrange
        val list = listOf(
            OperationSummaryDomain(
                id = 1,
                type = OperationTypeDomain.ADD,
                timeIso = "2025-08-20T00:00:00",
                conductor = ConductorDomain(1, "N", "F", "G", "E-1", "img"),
                productLineQuantity = 1,
                totalPrice = 0.0
            ),
            OperationSummaryDomain(
                id = 2,
                type = OperationTypeDomain.SOLD_CASH,
                timeIso = "2025-08-21T00:00:00",
                conductor = ConductorDomain(2, "M", "L", "K", "E-2", "img"),
                productLineQuantity = 2,
                totalPrice = 7.9
            )
        )
        whenever(repository.getPagedOperationSummaries(any()))
            .thenReturn(flowOf(PagingData.from(list)))

        val useCase = GetPagedOperationSummariesUseCase(repository)

        // Act
        val result = useCase.invoke(pageSize = 30).asSnapshot()

        // Assert
        assertEquals(2, result.size)
        assertEquals(2, result[1].id)
        assertEquals(OperationTypeDomain.SOLD_CASH, result[1].type)
    }

    /**
     * Техника тест-дизайна: #2 Граничные значения
     *
     * Автор: OwletsFox
     *
     * Описание:
     *   - Проверяется, что юзкейс передаёт в репозиторий корректный PagingConfig (pageSize).
     */
    @Test
    fun `GetPagedOperationSummariesUseCase passes correct PagingConfig`() = runTest {
        // Arrange
        whenever(repository.getPagedOperationSummaries(any()))
            .thenReturn(flowOf(PagingData.from(emptyList())))

        val useCase = GetPagedOperationSummariesUseCase(repository)

        // Act
        useCase.invoke(pageSize = 50).first()

        // Assert
        val captor = argumentCaptor<PagingConfig>()
        verify(repository).getPagedOperationSummaries(captor.capture())
        assertEquals(50, captor.firstValue.pageSize)
    }

    /**
     * Техника тест-дизайна: #3 Классы эквивалентности (второй юзкейс)
     *
     * Автор: OwletsFox
     *
     * Описание:
     *   - ObserveOperationItemsUseCase возвращает Flow<CartDomain>, который отдаёт репозиторий.
     */
    @Test
    fun `ObserveOperationItemsUseCase returns flow from repository`() = runTest {
        // Arrange
        val domain = CartDomain(
            items = listOf(
                CartItemDomain(
                    product = ProductInfoDomain(
                        id = 10, name = "Cola", description = "0.33", image = "img://cola", price = 1.2
                    ),
                    quantity = 2
                )
            )
        )
        whenever(repository.observeOperationItems(42))
            .thenReturn(flowOf(domain))

        val useCase = ObserveOperationItemsUseCase(repository)

        // Act
        val result = useCase.invoke(42).first()

        // Assert
        assertEquals(1, result.items.size)
        assertEquals(10, result.items.first().product.id)
        assertEquals(2, result.items.first().quantity)
    }
}
