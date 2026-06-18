package com.chaikasoft.app.domain.usecases

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.chaikasoft.app.data.datasource.repo.ChaikaSoftApiServiceRepositoryInterface
import com.chaikasoft.app.data.datasource.repo.TemplatePagingSource
import com.chaikasoft.app.data.inmemory.InMemoryCartRepositoryInterface
import com.chaikasoft.app.data.room.repo.RoomProductInfoRepositoryInterface
import com.chaikasoft.app.domain.common.AppError
import com.chaikasoft.app.domain.common.RemoteResult
import com.chaikasoft.app.domain.models.CartItemDomain
import com.chaikasoft.app.domain.models.ResolvedTemplateDetailDomain
import com.chaikasoft.app.domain.models.ResolvedTemplateItemDomain
import com.chaikasoft.app.domain.models.TemplateDomain
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow

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

class GetTemplatesUseCase @Inject constructor(
    private val repository: ChaikaSoftApiServiceRepositoryInterface
) {
    suspend operator fun invoke(
        query: String = "",
        limit: Int = 100,
        offset: Int = 0
    ): RemoteResult<List<TemplateDomain>> = repository.fetchTemplates(query, limit, offset)
}

class GetTemplateDetailUseCase @Inject constructor(
    private val repository: ChaikaSoftApiServiceRepositoryInterface
) {
    suspend operator fun invoke(templateId: Int): RemoteResult<TemplateDomain> =
        repository.fetchTemplateDetail(templateId)
}

class GetResolvedTemplateDetailUseCase @Inject constructor(
    private val getTemplateDetailUseCase: GetTemplateDetailUseCase,
    private val getProductsByIdsUseCase: GetProductsByIdsUseCase
) {
    suspend operator fun invoke(templateId: Int): RemoteResult<ResolvedTemplateDetailDomain> =
        when (val templateResult = getTemplateDetailUseCase(templateId)) {
            is RemoteResult.Failure -> templateResult
            is RemoteResult.Success -> resolveProducts(templateResult.data)
        }

    private suspend fun resolveProducts(
        template: TemplateDomain
    ): RemoteResult<ResolvedTemplateDetailDomain> = runCatching {
        val productsById = getProductsByIdsUseCase(
            template.content.map { it.productId }.distinct()
        )

        ResolvedTemplateDetailDomain(
            template = template,
            items = template.content.map { content ->
                ResolvedTemplateItemDomain(
                    productId = content.productId,
                    quantity = content.quantity,
                    product = productsById[content.productId]
                )
            }
        )
    }.fold(
        onSuccess = { detail -> RemoteResult.Success(detail) },
        onFailure = { error -> error.toTemplateDetailFailure() }
    )

    private fun Throwable.toTemplateDetailFailure(): RemoteResult.Failure {
        if (this is CancellationException) throw this
        if (this is Error) throw this
        val exception = this as? Exception ?: Exception(this)
        return RemoteResult.Failure(AppError.Unknown(exception))
    }
}

class ApplyTemplateUseCase @Inject constructor(
    private val productInfoRepository: RoomProductInfoRepositoryInterface
) {
    suspend operator fun invoke(cart: InMemoryCartRepositoryInterface, template: TemplateDomain) {
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
