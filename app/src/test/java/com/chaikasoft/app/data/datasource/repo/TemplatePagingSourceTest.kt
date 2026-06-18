package com.chaikasoft.app.data.datasource.repo

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.chaikasoft.app.domain.common.AppError
import com.chaikasoft.app.domain.common.AppErrorException
import com.chaikasoft.app.domain.common.RemoteResult
import com.chaikasoft.app.domain.models.TemplateDomain
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest

class TemplatePagingSourceTest : FunSpec({

    lateinit var repository: ChaikaSoftApiServiceRepositoryInterface
    lateinit var source: TemplatePagingSource

    beforeTest {
        repository = mockk()
        source = TemplatePagingSource(repository, query = "tea")
    }

    test("load first page returns Page with prevKey null and nextKey offset") {
        runTest {
            coEvery { repository.fetchTemplates(query = "tea", limit = 2, offset = 0) } returns
                RemoteResult.Success(
                    listOf(
                        template(1),
                        template(2),
                    )
                )

            val result = source.load(
                PagingSource.LoadParams.Refresh(key = null, loadSize = 2, placeholdersEnabled = false),
            )

            val page = result as PagingSource.LoadResult.Page
            page.data.map { it.id } shouldBe listOf(1, 2)
            page.prevKey shouldBe null
            page.nextKey shouldBe 2
        }
    }

    test("load non-first page returns Page with prevKey offset and nextKey null on last page") {
        runTest {
            coEvery { repository.fetchTemplates(query = "tea", limit = 2, offset = 2) } returns
                RemoteResult.Success(listOf(template(3)))

            val result = source.load(
                PagingSource.LoadParams.Append(key = 2, loadSize = 2, placeholdersEnabled = false),
            )

            val page = result as PagingSource.LoadResult.Page
            page.data.map { it.id } shouldBe listOf(3)
            page.prevKey shouldBe 0
            page.nextKey shouldBe null
        }
    }

    test("load returns Error when repository returns RemoteResult.Failure") {
        runTest {
            coEvery { repository.fetchTemplates(query = "tea", limit = 2, offset = 0) } returns
                RemoteResult.Failure(AppError.Network)

            val result = source.load(
                PagingSource.LoadParams.Refresh(key = null, loadSize = 2, placeholdersEnabled = false),
            )

            val error = (result as PagingSource.LoadResult.Error).throwable as AppErrorException
            error.error shouldBe AppError.Network
        }
    }

    test("getRefreshKey uses prevKey plus pageSize when available") {
        val state = PagingState(
            pages = listOf(
                PagingSource.LoadResult.Page(
                    data = listOf(template(1)),
                    prevKey = 20,
                    nextKey = 60,
                ),
            ),
            anchorPosition = 0,
            config = PagingConfig(pageSize = 20),
            leadingPlaceholderCount = 0,
        )

        val refreshKey = source.getRefreshKey(state)

        refreshKey shouldBe 40
    }

    test("getRefreshKey uses nextKey minus pageSize when prevKey is null") {
        val state = PagingState(
            pages = listOf(
                PagingSource.LoadResult.Page(
                    data = listOf(template(1)),
                    prevKey = null,
                    nextKey = 40,
                ),
            ),
            anchorPosition = 0,
            config = PagingConfig(pageSize = 20),
            leadingPlaceholderCount = 0,
        )

        val refreshKey = source.getRefreshKey(state)

        refreshKey shouldBe 20
    }
})

private fun template(id: Int) =
    TemplateDomain(
        id = id,
        templateName = "template-$id",
        description = "desc-$id",
        content = emptyList(),
    )
