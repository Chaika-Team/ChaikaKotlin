@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.example.chaika.data.room.repo

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.testing.asSnapshot
import com.example.chaika.data.room.dao.CartOperationDao
import com.example.chaika.data.room.entities.CartItem
import com.example.chaika.data.room.entities.CartOperation
import com.example.chaika.data.room.entities.OperationInfoView
import com.example.chaika.data.room.entities.ProductInfo
import com.example.chaika.data.room.mappers.toInt
import com.example.chaika.data.room.relations.CartItemWithProduct
import com.example.chaika.domain.models.OperationTypeDomain
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

/**
 * Техника тест-дизайна: #1 Классы эквивалентности
 *
 * Автор: OwletsFox
 *
 * Описание:
 *   - Тест для RoomCartOperationRepository: позитивный сценарий, когда DAO возвращает корректный список операций.
 */
class RoomCartOperationRepositoryTest {
    private val cartOperationDao = mock(CartOperationDao::class.java)
    private val repository = RoomCartOperationRepository(cartOperationDao)

    @Test
    fun testGetCartOperationReportsWithIds_positive() =
        runBlocking {
            // Arrange
            val operations =
                listOf(
                    CartOperation(
                        id = 10,
                        operationType = OperationTypeDomain.ADD.toInt(),
                        operationTime = "2025-02-26T10:00:00",
                        conductorId = 101,
                    ),
                    CartOperation(
                        id = 20,
                        operationType = OperationTypeDomain.SOLD_CASH.toInt(),
                        operationTime = "2025-02-26T11:00:00",
                        conductorId = 102,
                    ),
                )
            whenever(cartOperationDao.getAllOperations()).thenReturn(flowOf(operations))

            // Act: получаем первую эмиссию Flow
            val result = repository.getCartOperationReportsWithIds().first()

            // Assert
            assertEquals(2, result.size)
            val firstPair = result[0]
            assertEquals(10, firstPair.first)
            assertEquals("101", firstPair.second.employeeID)
        }

    /**
     * Техника тест-дизайна: #2 Граничные значения
     *
     * Автор: OwletsFox
     *
     * Описание:
     *   - Тест для RoomCartOperationRepository: сценарий пустого списка.
     *   - Граничные значения: если DAO возвращает пустой список, то первая эмиссия содержит пустой список.
     */
    @Test
    fun testGetCartOperationReportsWithIds_empty() =
        runBlocking {
            // Arrange
            whenever(cartOperationDao.getAllOperations()).thenReturn(flowOf(emptyList()))

            // Act
            val result = repository.getCartOperationReportsWithIds().first()

            // Assert
            assertEquals(0, result.size)
        }

    // Вспомогательный PagingSource для имитации ответа DAO
    private class FakePagingSource<T : Any>(
        private val data: List<T>
    ) : PagingSource<Int, T>() {
        override fun getRefreshKey(state: PagingState<Int, T>): Int = 0
        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
            val start = params.key ?: 0
            val end = (start + params.loadSize).coerceAtMost(data.size)
            val sub = if (start in 0..end) data.subList(start, end) else emptyList()
            val prevKey = if (start == 0) null else (start - params.loadSize).coerceAtLeast(0)
            val nextKey = if (end >= data.size) null else end
            return LoadResult.Page(
                data = sub,
                prevKey = prevKey,
                nextKey = nextKey
            )
        }
    }

    /**
     * Техника тест-дизайна: #3 Пагинация «шапок» (классы эквивалентности)
     *
     * Автор: OwletsFox
     *
     * Описание:
     *   - Репозиторий возвращает постраничные «шапки» операций, смэпленные в домен.
     */
    @Test
    fun testGetPagedOperationSummaries_positive() = runBlocking {
        // Arrange
        val v2 = OperationInfoView(
            operationId = 101,
            operationType = OperationTypeDomain.SOLD_CART.ordinal,
            operationTime = "2025-08-21T09:10:00",
            conductorId = 5,
            conductorName = "John",
            conductorFamilyName = "Doe",
            conductorGivenName = "J.",
            productLineQuantity = 2,
            totalPrice = 9.0
        )
        val v1 = OperationInfoView(
            operationId = 100,
            operationType = OperationTypeDomain.ADD.ordinal,
            operationTime = "2025-08-20T08:00:00",
            conductorId = 5,
            conductorName = "John",
            conductorFamilyName = "Doe",
            conductorGivenName = "J.",
            productLineQuantity = 1,
            totalPrice = 10.0
        )

        whenever(cartOperationDao.getPagedOperationInfos()).thenReturn(FakePagingSource(listOf(v2, v1)))

        // Act
        val result = repository
            .getPagedOperationSummaries(PagingConfig(pageSize = 2))
            .asSnapshot() // собираем Flow<PagingData<...>> в List

        // Assert
        assertEquals(2, result.size)
        assertEquals(101, result[0].id)
        assertEquals(OperationTypeDomain.SOLD_CART, result[0].type)
        assertEquals(100, result[1].id)
        assertEquals(OperationTypeDomain.ADD, result[1].type)
    }

    /**
     * Техника тест-дизайна: #4 Пагинация «шапок» (граничные значения)
     *
     * Автор: OwletsFox
     *
     * Описание:
     *   - DAO возвращает пустой PagingSource → на выходе пустой список.
     */
    @Test
    fun testGetPagedOperationSummaries_empty() = runBlocking {
        // Arrange
        whenever(cartOperationDao.getPagedOperationInfos()).thenReturn(FakePagingSource(emptyList()))

        // Act
        val result = repository
            .getPagedOperationSummaries(PagingConfig(pageSize = 20))
            .asSnapshot()

        // Assert
        assertEquals(0, result.size)
    }

    /**
     * Техника тест-дизайна: #5 Наблюдение и маппинг (классы эквивалентности)
     *
     * Автор: OwletsFox
     *
     * Описание:
     *   - Репозиторий корректно маппит Flow<List<CartItemWithProduct>> в CartDomain.
     */
    @Test
    fun testObserveOperationItems_mapsToCartDomain() = runBlocking {
        // Arrange
        val p1 = ProductInfo(1, "Tea", "Green", "img://tea", 2.0)
        val p2 = ProductInfo(2, "Coffee", "Arabica", "img://coffee", 3.5)

        val l1 = listOf(
            CartItemWithProduct(
                item = CartItem(id = 1, cartOperationId = 42, productId = 1, impact = 2),
                product = p1
            ),
            CartItemWithProduct(
                item = CartItem(id = 2, cartOperationId = 42, productId = 2, impact = 1),
                product = p2
            )
        )

        whenever(cartOperationDao.observeItemsWithProducts(42)).thenReturn(flowOf(l1))

        // Act
        val cart = repository.observeOperationItems(42).first()

        // Assert
        assertEquals(2, cart.items.size)
        assertEquals(1, cart.items[0].product.id)
        assertEquals("Tea", cart.items[0].product.name)
        assertEquals(2, cart.items[0].quantity)
        assertEquals(2, cart.items[1].product.id)
        assertEquals("Coffee", cart.items[1].product.name)
        assertEquals(1, cart.items[1].quantity)
    }

    /**
     * Техника тест-дизайна: #6 Наблюдение и маппинг (граничные значения)
     *
     * Автор: OwletsFox
     *
     * Описание:
     *   - Если DAO отдаёт пустой список позиций, CartDomain должен быть пустым.
     */
    @Test
    fun testObserveOperationItems_empty() = runBlocking {
        // Arrange
        whenever(cartOperationDao.observeItemsWithProducts(99)).thenReturn(flowOf(emptyList()))

        // Act
        val cart = repository.observeOperationItems(99).first()

        // Assert
        assertEquals(0, cart.items.size)
    }
}
