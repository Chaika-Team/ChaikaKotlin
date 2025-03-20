package com.example.chaika.data.room.repo

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.chaika.data.room.dao.ProductInfoDao
import com.example.chaika.data.room.mappers.toDomain
import com.example.chaika.data.room.mappers.toEntity
import com.example.chaika.domain.models.ProductInfoDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomProductInfoRepository @Inject constructor(
    private val productInfoDao: ProductInfoDao,
) : RoomProductInfoRepositoryInterface {

    override fun getAllProducts(): Flow<List<ProductInfoDomain>> {
        return productInfoDao.getAllProducts().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun insertProduct(product: ProductInfoDomain) {
        productInfoDao.insertProduct(product.toEntity())
    }

    override suspend fun updateProduct(product: ProductInfoDomain) {
        productInfoDao.updateProduct(product.toEntity())
    }

    override suspend fun deleteProduct(product: ProductInfoDomain) {
        productInfoDao.deleteProduct(product.toEntity())
    }

    override fun getPagedProducts(): PagingSource<Int, ProductInfoDomain> {
        val originalSource = productInfoDao.getPagedProducts()
        return object : PagingSource<Int, ProductInfoDomain>() {
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProductInfoDomain> {
                return when (val result = originalSource.load(params)) {
                    is LoadResult.Page -> LoadResult.Page(
                        data = result.data.map { it.toDomain() },
                        prevKey = result.prevKey,
                        nextKey = result.nextKey,
                        itemsBefore = result.itemsBefore,
                        itemsAfter = result.itemsAfter
                    )

                    is LoadResult.Error -> LoadResult.Error(result.throwable)
                    // Обрабатываем все остальные случаи (например, если будет добавлена новая ветка)
                    else -> LoadResult.Error(Throwable("Unexpected load result: $result"))
                }
            }

            override fun getRefreshKey(state: PagingState<Int, ProductInfoDomain>): Int? {
                return state.anchorPosition?.let { anchorPosition ->
                    state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                        ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
                }
            }
        }
    }

    override suspend fun getProductById(productId: Int): ProductInfoDomain? {
        return productInfoDao.getProductById(productId)?.toDomain()
    }

}
