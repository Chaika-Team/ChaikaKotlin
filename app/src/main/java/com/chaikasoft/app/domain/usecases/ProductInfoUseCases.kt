package com.chaikasoft.app.domain.usecases

import android.util.Log
import androidx.paging.PagingData
import com.chaikasoft.app.data.datasource.repo.ChaikaSoftApiServiceRepositoryInterface
import com.chaikasoft.app.data.local.ImageSubDir
import com.chaikasoft.app.data.local.LocalImageRepositoryInterface
import com.chaikasoft.app.data.room.repo.RoomProductInfoRepositoryInterface
import com.chaikasoft.app.data.room.repo.RoomSyncMetaRepositoryInterface
import com.chaikasoft.app.data.room.sync.SyncDataset
import com.chaikasoft.app.di.IoDispatcher
import com.chaikasoft.app.domain.common.RemoteResult
import com.chaikasoft.app.domain.models.ProductInfoDomain
import com.chaikasoft.app.domain.sealed.RefreshProductsResult
import com.chaikasoft.app.util.normalizedRemoteImageUrlOrNull
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

// Юзкейс для получения всех товаров из базы данных.
// Используйте GetPagedProductsUseCase, чтобы улучшить оптимизацию
class GetAllProductsUseCase @Inject constructor(
    private val roomProductInfoRepositoryInterface: RoomProductInfoRepositoryInterface
) {
    operator fun invoke(): Flow<List<ProductInfoDomain>> =
        roomProductInfoRepositoryInterface.getAllProducts()
}

/**
 *
 * Use case для получения списка товаров с поддержкой бесконечной прокрутки.
 *
 * Использует PagingSource, полученный из репозитория.
 */
class GetPagedProductsUseCase @Inject constructor(
    private val repository: RoomProductInfoRepositoryInterface
) {
    operator fun invoke(
        query: String? = null,
        pageSize: Int = 20
    ): Flow<PagingData<ProductInfoDomain>> = repository.getPagedProducts(query, pageSize)
}

// Юзкейс для удаления товара из базы данных
class DeleteProductUseCase @Inject constructor(
    private val repository: RoomProductInfoRepositoryInterface
) {
    suspend operator fun invoke(productInfoDomain: ProductInfoDomain) {
        repository.deleteProduct(productInfoDomain)
    }
}

/**
 * Use case для получения списка товаров из ChaikaSoft.
 *
 * Обращается к ChaikaSoftApiServiceRepositoryInterface, который использует ChaikaSoftApiService для запроса.
 * По умолчанию возвращает до 100 товаров, начиная с offset = 0.
 */
class FetchProductsFromServerUseCase @Inject constructor(
    private val repository: ChaikaSoftApiServiceRepositoryInterface
) {
    suspend operator fun invoke(
        limit: Int = 100,
        offset: Int = 0
    ): RemoteResult<List<ProductInfoDomain>> = repository.fetchProducts(limit, offset)
}

/**
 * Объединённый use case для получения товаров с сервера и их сохранения локально.
 *
 * Сначала получает список товаров с сервера с помощью [FetchProductsFromServerUseCase],
 * а затем сохраняет их локально через [SaveProductsLocallyUseCase].
 */
class RefreshProductsOnLaunchUseCase @Inject constructor(
    private val fetchProductsFromServerUseCase: FetchProductsFromServerUseCase,
    private val productInfoRepository: RoomProductInfoRepositoryInterface,
    private val syncMetaRepo: RoomSyncMetaRepositoryInterface,
    private val saveProductsLocallyUseCase: SaveProductsLocallyUseCase,
    private val hasActiveShift: HasActiveShiftUseCase,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(limit: Int = 100, offset: Int = 0): RefreshProductsResult =
        withContext(ioDispatcher) {
            if (hasActiveShift()) {
                return@withContext RefreshProductsResult.SkippedActiveShift
            }

            if (!shouldRefreshProducts()) {
                return@withContext RefreshProductsResult.SkippedFreshCache
            }

            when (val remote = fetchProductsFromServerUseCase(limit, offset)) {
                is RemoteResult.Failure -> RefreshProductsResult.RemoteFailure(remote.error)
                is RemoteResult.Success -> saveProductsIfChanged(remote.data)
            }
        }

    private suspend fun shouldRefreshProducts(): Boolean {
        val hasAnyProducts = productInfoRepository.hasAnyProductsOnce()
        if (!hasAnyProducts) return true

        val lastSuccessfulSyncAt =
            syncMetaRepo.getLastSuccessfulSyncAt(SyncDataset.PRODUCTS.key) ?: return true

        return System.currentTimeMillis() - lastSuccessfulSyncAt >= SyncDataset.PRODUCTS.ttlMs
    }

    private suspend fun saveProductsIfChanged(
        remoteProducts: List<ProductInfoDomain>
    ): RefreshProductsResult = try {
        saveProductsLocallyUseCase(remoteProducts)
        syncMetaRepo.setLastSuccessfulSyncAt(
            datasetKey = SyncDataset.PRODUCTS.key,
            timestampMillis = System.currentTimeMillis()
        )
        RefreshProductsResult.Success(productCount = remoteProducts.size)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        RefreshProductsResult.LocalFailure(e)
    }
}

/**
 * Use case для сохранения списка товаров локально.
 *
 * Для каждого товара:
 * 1. Скачивает изображение через LocalImageRepositoryInterface, сохраняя его в директорию [ImageSubDir.PRODUCTS.folder].
 * 2. Обновляет доменную модель [ProductInfoDomain], заменяя поле image на локальный путь (если удалось сохранить).
 * 3. Сохраняет обновлённый товар в базу данных через RoomProductInfoRepositoryInterface.
 */
class SaveProductsLocallyUseCase @Inject constructor(
    private val productInfoRepository: RoomProductInfoRepositoryInterface,
    private val localImageRepository: LocalImageRepositoryInterface,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(products: List<ProductInfoDomain>): List<ProductInfoDomain> =
        withContext(ioDispatcher) {
            val localProducts = productInfoRepository.getAllProductsOnce()
            val localProductsById = localProducts.associateBy { it.id }
            val productsForLocalStorage = products.map { product ->
                product.withLocalImage(localProductsById[product.id])
            }

            val shouldUpsert =
                productsForLocalStorage.sortedBy { it.id } != localProducts.sortedBy { it.id }
            if (shouldUpsert) {
                productInfoRepository.upsertAll(productsForLocalStorage)
            }

            Log.i(
                PRODUCT_IMAGE_SYNC_LOG_TAG,
                "Products local save summary: total=${products.size}, upserted=$shouldUpsert"
            )
            productsForLocalStorage
        }

    private suspend fun ProductInfoDomain.withLocalImage(
        existingProduct: ProductInfoDomain?
    ): ProductInfoDomain {
        val remoteImageUrl = image.normalizedRemoteImageUrlOrNull() ?: return copy(image = "")
        val existingImage = existingProduct?.image?.takeIf { it.isNotBlank() }
        val existingRemoteImageUrl = existingImage?.normalizedRemoteImageUrlOrNull()

        if (existingImage != null && existingRemoteImageUrl == remoteImageUrl) {
            return copy(image = existingImage)
        }

        val imagePath = localImageRepository.saveImageFromUrl(
            imageUrl = remoteImageUrl,
            fileName = "$name.jpg",
            subDir = ImageSubDir.PRODUCTS.folder
        )
        return copy(image = imagePath ?: remoteImageUrl)
    }
}

private const val PRODUCT_IMAGE_SYNC_LOG_TAG = "ProductImageSync"
