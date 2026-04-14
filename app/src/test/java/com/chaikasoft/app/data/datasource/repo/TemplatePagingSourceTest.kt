package com.chaikasoft.app.data.datasource.repo

import androidx.paging.PagingSource
import androidx.paging.PagingState
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

    /**
     * Техника тест-дизайна: #3 Таблица решений
     *
     * Описание:
     *   - Комбинация: первая страница, данных ровно pageSize.
     *   - Ожидаемое поведение: prevKey = null, nextKey = currentPage + 1.
     *   - Цель: зафиксировать базовую пагинационную ветку load().
     */
    test("load first page returns Page with prevKey null and nextKey set") {
        runTest {
            coEvery { repository.fetchTemplates(query = "tea", limit = 2, offset = 0) } returns
                listOf(
                    template(1),
                    template(2),
                )

            val result = source.load(
                PagingSource.LoadParams.Refresh(key = null, loadSize = 2, placeholdersEnabled = false),
            )

            val page = result as PagingSource.LoadResult.Page
            page.data.map { it.id } shouldBe listOf(1, 2)
            page.prevKey shouldBe null
            page.nextKey shouldBe 1
        }
    }

    /**
     * Техника тест-дизайна: #2 Граничные значения
     *
     * Описание:
     *   - Граница: получено меньше элементов, чем pageSize.
     *   - Ожидаемое поведение: это последняя страница => nextKey = null.
     *   - Цель: зафиксировать корректное завершение пагинации.
     */
    test("load non-first page returns Page with prevKey and nextKey null on last page") {
        runTest {
            coEvery { repository.fetchTemplates(query = "tea", limit = 2, offset = 2) } returns
                listOf(template(3))

            val result = source.load(
                PagingSource.LoadParams.Append(key = 1, loadSize = 2, placeholdersEnabled = false),
            )

            val page = result as PagingSource.LoadResult.Page
            page.data.map { it.id } shouldBe listOf(3)
            page.prevKey shouldBe 0
            page.nextKey shouldBe null
        }
    }

    /**
     * Техника тест-дизайна: #5 Error Guessing
     *
     * Описание:
     *   - Сценарий: репозиторий выбрасывает исключение в load().
     *   - Ожидаемое поведение: PagingSource возвращает LoadResult.Error.
     *   - Цель: зафиксировать контракт обработки ошибок в paging слое.
     */
    test("load returns Error when repository throws") {
        runTest {
            val error = IllegalStateException("boom")
            coEvery { repository.fetchTemplates(query = "tea", limit = 2, offset = 0) } throws error

            val result = source.load(
                PagingSource.LoadParams.Refresh(key = null, loadSize = 2, placeholdersEnabled = false),
            )

            (result as PagingSource.LoadResult.Error).throwable shouldBe error
        }
    }

    /**
     * Техника тест-дизайна: #4 Переходы состояний
     *
     * Описание:
     *   - Сценарий refresh при наличии prevKey у ближайшей страницы.
     *   - Ожидаемое поведение: refreshKey = prevKey + 1.
     *   - Цель: зафиксировать стабильную стратегию восстановления ключа.
     */
    test("getRefreshKey uses prevKey plus one when available") {
        val state = PagingState(
            pages = listOf(
                PagingSource.LoadResult.Page(
                    data = listOf(template(1)),
                    prevKey = 2,
                    nextKey = 4,
                ),
            ),
            anchorPosition = 0,
            config = androidx.paging.PagingConfig(pageSize = 20),
            leadingPlaceholderCount = 0,
        )

        val refreshKey = source.getRefreshKey(state)

        refreshKey shouldBe 3
    }

    /**
     * Техника тест-дизайна: #4 Переходы состояний
     *
     * Описание:
     *   - Сценарий refresh когда prevKey отсутствует, но есть nextKey.
     *   - Ожидаемое поведение: refreshKey = nextKey - 1.
     *   - Цель: покрыть вторую ветку расчета refreshKey.
     */
    test("getRefreshKey uses nextKey minus one when prevKey is null") {
        val state = PagingState(
            pages = listOf(
                PagingSource.LoadResult.Page(
                    data = listOf(template(1)),
                    prevKey = null,
                    nextKey = 5,
                ),
            ),
            anchorPosition = 0,
            config = androidx.paging.PagingConfig(pageSize = 20),
            leadingPlaceholderCount = 0,
        )

        val refreshKey = source.getRefreshKey(state)

        refreshKey shouldBe 4
    }
})

private fun template(id: Int) =
    TemplateDomain(
        id = id,
        templateName = "template-$id",
        description = "desc-$id",
        content = emptyList(),
    )
