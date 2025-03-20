package com.example.chaika.data.dataSource.repo

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.chaika.domain.models.TemplateDomain
import javax.inject.Inject

/**
 * PagingSource для загрузки шаблонов с ChaikaSoft API.
 *
 * При каждом вызове load() выполняется запрос к API через репозиторий,
 * используя переданный поисковый запрос, размер страницы и вычисленный offset.
 *
 * @param repository Репозиторий, который обеспечивает доступ к ChaikaSoft API.
 * @param query Поисковый запрос для фильтрации шаблонов. По умолчанию пустая строка.
 */
class TemplatePagingSource @Inject constructor(
    private val repository: ChaikaSoftApiServiceRepositoryInterface,
    private val query: String = ""
) : PagingSource<Int, TemplateDomain>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TemplateDomain> {
        return try {
            // Определяем текущую страницу и размер страницы
            val currentPage = params.key ?: 0
            val pageSize = params.loadSize

            // Вычисляем offset для запроса
            val offset = currentPage * pageSize

            // Получаем список шаблонов с сервера через репозиторий.
            // Если query пустой, возвращаются все шаблоны, иначе — отфильтрованные по поиску.
            val templates = repository.fetchTemplates(query, limit = pageSize, offset = offset)

            // Определяем ключ для следующей страницы: если список пустой, следующей страницы нет.
            val nextKey = if (templates.isEmpty()) null else currentPage + 1

            // Возвращаем успешный результат загрузки страницы
            LoadResult.Page(
                data = templates,
                prevKey = if (currentPage == 0) null else currentPage - 1,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            // В случае ошибки возвращаем LoadResult.Error
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, TemplateDomain>): Int? {
        // Для обновления страницы ищем ближайшую страницу к текущей позиции якоря
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
