package com.example.chaika.domain.usecases

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.chaika.data.dataSource.repo.ChaikaSoftApiServiceRepositoryInterface
import com.example.chaika.data.dataSource.repo.TemplatePagingSource
import com.example.chaika.data.inMemory.InMemoryCartRepositoryInterface
import com.example.chaika.data.room.repo.RoomProductInfoRepositoryInterface
import com.example.chaika.domain.models.CartItemDomain
import com.example.chaika.domain.models.TemplateDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Use case для получения списка шаблонов с поддержкой бесконечной прокрутки.
 * Использует PagingSource для динамической загрузки шаблонов по мере прокрутки.
 *
 * @param repository Репозиторий, реализующий ChaikaSoftApiServiceRepositoryInterface.
 */
class GetPagedTemplatesUseCase @Inject constructor(
    private val repository: ChaikaSoftApiServiceRepositoryInterface
) {
    operator fun invoke(query: String = "", pageSize: Int = 20): Flow<PagingData<TemplateDomain>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { TemplatePagingSource(repository, query) }
        ).flow
    }
}

/**
 * Use case для применения шаблона к корзине.
 *
 * Принимает полный объект шаблона (TemplateDomain) с заполненным content,
 * преобразует содержимое шаблона в элементы корзины (CartItemDomain) и обновляет in-memory корзину.
 *
 * @param productInfoRepository Репозиторий для работы с продуктами.
 * @param inMemoryCartRepository Репозиторий для работы с корзиной в оперативной памяти.
 */
class ApplyTemplateUseCase @Inject constructor(
    private val productInfoRepository: RoomProductInfoRepositoryInterface,
    private val inMemoryCartRepository: InMemoryCartRepositoryInterface,
) {
    suspend operator fun invoke(template: TemplateDomain) = withContext(Dispatchers.IO) {
        // Преобразование каждого элемента шаблона в элемент корзины
        val cartItems = template.content.map { templateContent ->
            val productInfo = productInfoRepository.getProductById(templateContent.productId)
                ?: throw Exception("Product not found for id: ${templateContent.productId}")
            CartItemDomain(
                product = productInfo,
                quantity = templateContent.quantity
            )
        }
        // Обновляем in-memory корзину: очищаем и добавляем новые элементы
        inMemoryCartRepository.clearCart()
        cartItems.forEach { item ->
            inMemoryCartRepository.addItemToCart(item)
        }
    }
}