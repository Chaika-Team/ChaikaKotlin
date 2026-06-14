package com.chaikasoft.app.data.datasource.repo

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.chaikasoft.app.domain.common.AppErrorException
import com.chaikasoft.app.domain.common.RemoteResult
import com.chaikasoft.app.domain.models.TemplateDomain
import javax.inject.Inject
import kotlinx.coroutines.CancellationException

class TemplatePagingSource @Inject constructor(
    private val repository: ChaikaSoftApiServiceRepositoryInterface,
    private val query: String = ""
) : PagingSource<Int, TemplateDomain>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TemplateDomain> =
        runCatching {
            val offset = params.key ?: 0
            val loadSize = params.loadSize

            when (
                val remote = repository.fetchTemplates(query, limit = loadSize, offset = offset)
            ) {
                is RemoteResult.Success -> remote.data.toPage(offset, loadSize)
                is RemoteResult.Failure -> {
                    LoadResult.Error(AppErrorException(remote.error))
                }
            }
        }.fold(
            onSuccess = { result -> result },
            onFailure = { error -> error.toLoadResultError() }
        )

    override fun getRefreshKey(state: PagingState<Int, TemplateDomain>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(state.config.pageSize)
                ?: state.closestPageToPosition(
                    anchorPosition
                )?.nextKey?.minus(state.config.pageSize)
        }

    private fun List<TemplateDomain>.toPage(
        offset: Int,
        loadSize: Int
    ): LoadResult.Page<Int, TemplateDomain> = LoadResult.Page(
        data = this,
        prevKey = if (offset == 0) null else maxOf(offset - loadSize, 0),
        nextKey = if (size < loadSize) null else offset + size
    )

    private fun Throwable.toLoadResultError(): LoadResult.Error<Int, TemplateDomain> {
        if (this is CancellationException) throw this
        if (this is Error) throw this
        return LoadResult.Error(this)
    }
}
