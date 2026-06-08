package com.chaikasoft.app.domain.usecases

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.chaikasoft.app.data.datasource.repo.ChaikaSoftApiServiceRepositoryInterface
import com.chaikasoft.app.data.datasource.repo.TemplatePagingSource
import com.chaikasoft.app.data.inmemory.InMemoryCartRepositoryInterface
import com.chaikasoft.app.data.room.repo.RoomProductInfoRepositoryInterface
import com.chaikasoft.app.domain.models.CartItemDomain
import com.chaikasoft.app.domain.models.ResolvedTemplateDetailDomain
import com.chaikasoft.app.domain.models.ResolvedTemplateItemDomain
import com.chaikasoft.app.domain.models.TemplateDomain
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

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
        Log.d("GetPagedTemplatesUC", "We into usecase")
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
 * Use case для получения всех шаблонов без пагинации.
 *
 * Этот юзкейс напрямую вызывает метод репозитория, чтобы получить все доступные шаблоны.
 */
class GetTemplatesUseCase @Inject constructor(
    private val repository: ChaikaSoftApiServiceRepositoryInterface
) {
    suspend operator fun invoke(
        query: String = "",
        limit: Int = 100,
        offset: Int = 0
    ): List<TemplateDomain> {
        // Используем большой limit для получения всех шаблонов
        return repository.fetchTemplates(query, limit, offset)
    }
}

/**
 * Use case для получения детальной информации о шаблоне.
 *
 * Вызывает репозиторный метод fetchTemplateDetail для получения шаблона с заполненным content,
 * что необходимо для корректного применения шаблона к корзине.
 *
 * @param repository Репозиторий, реализующий ChaikaSoftApiServiceRepositoryInterface.
 */
class GetTemplateDetailUseCase @Inject constructor(
    private val repository: ChaikaSoftApiServiceRepositoryInterface
) {
    suspend operator fun invoke(templateId: Int): TemplateDomain =
        repository.fetchTemplateDetail(templateId)
}

/**
 * Use case для получения детальной информации о шаблоне с локальными данными товаров.
 *
 * Оркестрирует [GetTemplateDetailUseCase] и [GetProductsByIdsUseCase]: получает сырой шаблон
 * из сервиса, затем разрешает productId из содержимого шаблона в товары из локальной базы.
 */
class GetResolvedTemplateDetailUseCase @Inject constructor(
    private val getTemplateDetailUseCase: GetTemplateDetailUseCase,
    private val getProductsByIdsUseCase: GetProductsByIdsUseCase
) {
    suspend operator fun invoke(templateId: Int): ResolvedTemplateDetailDomain {
        val template = getTemplateDetailUseCase(templateId)
        val productsById = getProductsByIdsUseCase(
            template.content.map { it.productId }.distinct()
        )

        return ResolvedTemplateDetailDomain(
            template = template,
            items = template.content.map { content ->
                ResolvedTemplateItemDomain(
                    productId = content.productId,
                    quantity = content.quantity,
                    product = productsById[content.productId]
                )
            }
        )
    }
}

/**
 * Use case для применения шаблона к корзине.
 *
 * Сначала проверяет, что все товары из шаблона есть в локальной базе данных, и только после
 * этого очищает корзину и добавляет элементы шаблона. Это защищает текущую корзину от потери,
 * если шаблон ссылается на отсутствующий товар.
 */
class ApplyTemplateUseCase @Inject constructor(
    private val productInfoRepository: RoomProductInfoRepositoryInterface
) {
    suspend operator fun invoke(cart: InMemoryCartRepositoryInterface, template: TemplateDomain) {
        // Build all cart items before clearing the cart to avoid data loss on missing products.
        val productsById = productInfoRepository
            .getProductsByIds(template.content.map { it.productId }.distinct())
            .associateBy { it.id }
        val items = template.content.map { content ->
            val product = productsById[content.productId]
                ?: throw IllegalArgumentException(
                    "Product with id=${content.productId} not found in local DB"
                )

            CartItemDomain(
                product = product,
                quantity = content.quantity
            )
        }

        cart.clearCart()

        items.forEach { cart.addItemToCart(it) }
    }
}
